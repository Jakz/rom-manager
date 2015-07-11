package jack.rm.data;

import jack.rm.Main;
import jack.rm.PersistenceRom;
import jack.rm.data.set.RomSet;
import jack.rm.files.*;
import jack.rm.gui.ProgressDialog;
import jack.rm.log.*;

import java.io.File;
import java.util.*;
import java.util.zip.*;
import java.io.FileInputStream;

import javax.swing.SwingWorker;

public class Scanner
{
	RomList list;
	
	private Set<File> existing = new HashSet<File>();
	private Set<File> foundFiles = new HashSet<File>();
	
	public Scanner(RomList list)
	{
		this.list = list;
	}
	
	public static long computeCRC(File file)
	{
		try
		{
			CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());

			byte[] buf = new byte[1024];
			
			while (cis.read(buf) >= 0);
			
			long crc = cis.getChecksum().getValue();
			
			cis.close();
			
			return crc;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void foundRom(ScanResult result)
	{
	  if (result == null)
	    return;
	  
	  Rom rom = result.rom;
	  
	  if (rom.status == RomStatus.FOUND)
	  {
	    Log.log(LogType.WARNING, LogSource.SCANNER, LogTarget.file(result.entry.file()), "File contains a rom already present in romset: "+rom.file);
	  }
	  else if (Organizer.isCorrectlyNamed(result.entry.plainName(), rom))
	    rom.status = RomStatus.FOUND;
	  else
	    rom.status = RomStatus.INCORRECT_NAME;
	  
	  rom.file = result.entry;
	}
	
	public ScanResult scanFile(File file)
	{
	  ScanResult result = null;
	  
	  if (file.getName().endsWith(".zip"))
    {	          
      if (!existing.contains(file))
      {    
  	    try (ZipFile zip = new ZipFile(file))
        {          
          Enumeration<? extends ZipEntry> enu = zip.entries();
          
          while (enu.hasMoreElements())
          {
            ZipEntry entry = enu.nextElement();
            long curCrc = entry.getCrc();
            
            Rom rom = list.getByCRC(curCrc);
            
            if (rom != null)
              result = new ScanResult(rom, new RomFileEntry.Archive(file, entry.getName()));
          }
        }
        catch (Exception e)
        {
          Log.log(LogType.ERROR, LogSource.SCANNER, LogTarget.file(file), "Zipped file is corrupt, skipping");
        }
      }
      
      return result;
    }
    else
    {
      long crc = computeCRC(file);
      String fileName = file.getName();
      fileName = fileName.substring(0, fileName.length()-4);
      
      Rom rom = list.getByCRC(crc);
      
      if (rom != null)
        return new ScanResult(rom, new RomFileEntry.Bin(file));
      else return null;
    }
	}
	
	public void scanForRoms(boolean total)
	{
		existing.clear();
		foundFiles.clear();
		
		if (total)
		{
			Log.log(LogType.MESSAGE, LogSource.SCANNER, LogTarget.romset(RomSet.current), "Scanning for roms");
			Main.romList.resetStatus();
		}
		else
		{
	    Log.log(LogType.MESSAGE, LogSource.SCANNER, LogTarget.romset(RomSet.current), "Scanning for new roms");

			int c = Main.romList.count();
			for (int j = 0; j < c; ++j)
			{
				Rom r = Main.romList.get(j);
				
				if (r.status != RomStatus.NOT_FOUND)
					existing.add(r.file.file());
			}
		}

		try
		{		
			File folder = RomSet.current.romPath().toFile();
			foundFiles = new FolderScanner(new CustomFileFilter(RomSet.current.type.exts)).scan(folder);
			ScannerWorker worker = new ScannerWorker(foundFiles);
			worker.execute();
		}
		catch (NullPointerException e)
		{
			Log.log(LogType.ERROR, LogSource.SCANNER, LogTarget.romset(RomSet.current), "Roms path doesn't exist! Scanning interrupted");
			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public class ScannerWorker extends SwingWorker<Void, Integer>
	{
	  private int total = 0;
	  private final List<File> files;
	  
	  ScannerWorker(Set<File> files)
	  {
	    this.files = new ArrayList<File>(files);
	    total = files.size();
	  }

	  @Override
	  public Void doInBackground()
	  {
	    ProgressDialog.init(Main.mainFrame, "Rom Scan", null);
	    
	    for (int i = 0; i < total; ++i)
	    {
	      setProgress((int)((((float)i)/total)*100));

	      File f = files.get(i);
	      ScanResult result = scanFile(f);
	      
	      foundRom(result);
	      Main.romList.updateStatus();
	      
	      publish(i);
	    }
	    
	    return null;
	  }
	  
	  @Override
	  public void process(List<Integer> v)
	  {
	    ProgressDialog.update(this, "Scanning "+v.get(v.size()-1)+" of "+foundFiles.size()+"..");
	    Main.mainFrame.updateTable();
	  }
	  
	  @Override
	  public void done()
	  {
	    PersistenceRom.consolidate(list);
	    ProgressDialog.finished();
	  }
	  
	}
}
