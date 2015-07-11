package jack.rm.data;

import jack.rm.Main;
import jack.rm.PersistenceRom;
import jack.rm.data.set.RomSet;
import jack.rm.files.*;
import jack.rm.gui.ProgressDialog;
import jack.rm.log.*;

import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.SwingWorker;

public class Scanner
{
	RomList list;
	
	private Set<Path> existing = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	private PathMatcher archiveMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{zip}");
	
	public Scanner(RomList list)
	{
		this.list = list;
	}
	
	public static long computeCRC(Path file)
	{
		try (CheckedInputStream cis = new CheckedInputStream(Files.newInputStream(file), new CRC32()))
		{
			byte[] buf = new byte[1024];
			
			while (cis.read(buf) >= 0);
			
			long crc = cis.getChecksum().getValue();
			
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
	  
	  if (rom.status != RomStatus.NOT_FOUND)
	  {
	    clones.add(result);
	    Log.log(LogType.WARNING, LogSource.SCANNER, LogTarget.file(result.entry.file()), "File contains a rom already present in romset: "+rom.entry);
	  }
	  else if (Organizer.isCorrectlyNamed(result.entry.plainName(), rom))
	    rom.status = RomStatus.FOUND;
	  else
	    rom.status = RomStatus.INCORRECT_NAME;
	  
	  rom.entry = result.entry;
	}
	
	public ScanResult scanFile(Path file)
	{
	  ScanResult result = null;
	  
	  if (archiveMatcher.matches(file.getFileName()))
    {	          	    
	    if (!existing.contains(file))
      {    
  	    try (ZipFile zip = new ZipFile(file.toFile()))
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
		clones.clear();
		
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
					existing.add(r.entry.file());
			}
		}

		try
		{		
			Path folder = RomSet.current.romPath();
			foundFiles = new FolderScanner(RomSet.current.getFileMatcher()).scan(folder);
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
	  private final List<Path> files;
	  
	  ScannerWorker(Set<Path> files)
	  {
	    this.files = new ArrayList<>(files);
	    total = files.size();
	  }

	  @Override
	  public Void doInBackground()
	  {
	    ProgressDialog.init(Main.mainFrame, "Rom Scan", () -> cancel(true) );
	    
	    for (int i = 0; i < total; ++i)
	    {
	      if (isCancelled())
	        return null;

	      setProgress((int)((((float)i)/total)*100));

	      Path f = files.get(i);
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
	    if (isCancelled())
        return;
      	    
	    ProgressDialog.update(this, "Scanning "+v.get(v.size()-1)+" of "+foundFiles.size()+"..");
	    Main.mainFrame.updateTable();
	  }
	  
	  @Override
	  public void done()
	  {
	    if (isCancelled())
	      return;

	    PersistenceRom.consolidate(list);
	    ProgressDialog.finished();
	  }
	  
	}
}
