package jack.rm.data;

import jack.rm.Main;
import jack.rm.PersistenceRom;
import jack.rm.data.set.RomSet;
import jack.rm.gui.ProgressDialog;

import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.zip.*;
import java.io.FileInputStream;

import javax.swing.SwingWorker;

public class Scanner
{
	RomList list;
	boolean scanSubdirectories = true;
	
	private Set<File> existing = new HashSet<File>();
	private Set<File> foundFiles = new HashSet<File>();
	
	private static class CustomFilter implements FileFilter
	{
		String[] exts;
		
		CustomFilter(String[] exts)
		{
			this.exts = exts;
		}
		
		public boolean accept(File file)
		{
			String name = file.getName();
			
			if (name.charAt(0) == '.')
				return false;
			
			if (file.isDirectory() || name.endsWith("zip"))
				return true;
			
			for (String s : exts)
			{
				if (name.endsWith(s))
					return true;
			}

			return true;
		}
	}
	
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
			
			return cis.getChecksum().getValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void scanFolder(File folder, CustomFilter filter)
	{
		File[] files = folder.listFiles(filter);
		
		for (int t = 0; t < files.length; ++t)
		{
			if (files[t].isDirectory())
			{
				scanFolder(files[t].getAbsoluteFile(), filter);
			}
			else 
			{
			  foundFiles.add(files[t]);
			}
		}
	}
	
	public void foundRom(File file, String fileName, Rom rom, RomType type)
	{
	  if (rom.status == RomStatus.FOUND)
	  {
      Main.logln("A duplicated rom has been found: "+rom.file+" and "+file+".");
	  }
	  else if (Renamer.isCorrectlyNamed(fileName, rom))
	  {
	    rom.status = RomStatus.FOUND;
	    ++Main.romList.countCorrect;
	  }
	  else
	  {
	    rom.status = RomStatus.INCORRECT_NAME;
	    ++Main.romList.countBadlyNamed;
	  }
	  
	  --Main.romList.countNotFound;
	  
	  rom.type = type;
	}
	
	public void scanFile(File file)
	{
	  if (file.getName().endsWith(".zip"))
    {
      try
      {
        if (!existing.contains(file))
        {         
          Enumeration<? extends ZipEntry> enu = new ZipFile(file).entries();
          String fileName = file.getName();
          fileName = fileName.substring(0, fileName.length()-4);
          
          while (enu.hasMoreElements())
          {
            ZipEntry entry = enu.nextElement();
            long curCrc = entry.getCrc();
            
            Rom rom = list.getByCRC(curCrc);
            
            if (rom != null)
            {
              Main.logln("Archive "+file.getName()+" contains file with CRC: "+curCrc+" that matches rom: "+rom.number+" "+rom.title);
              
              foundRom(file, fileName, rom, RomType.ZIP);
              rom.file = new RomFileEntry.Archive(file, entry.getName());
            }
          }
        }
      }
      catch (Exception e)
      {
        Main.logln("[ERROR] Zipped file "+file.getName()+" is corrupt. Skipping.");
      }
    }
    else
    {
      long crc = computeCRC(file);
      String fileName = file.getName();
      fileName = fileName.substring(0, fileName.length()-4);
      
      Rom rom = list.getByCRC(crc);
      
      if (rom != null)
      {
        foundRom(file, fileName, rom, RomType.BIN);
        rom.file = new RomFileEntry.Bin(file);
      }
    }
	}
	
	public void scanForRoms(boolean total)
	{
		existing.clear();
		foundFiles.clear();
		
		if (total)
		{
			Main.logln("Scanning for roms in path "+RomSet.current.romPath()+"...");
			Main.romList.resetStatus();
		}
		else
		{
			Main.logln("Scanning for new roms in path "+RomSet.current.romPath()+"...");
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
			File folder = new File(RomSet.current.romPath());
			scanFolder(folder, new CustomFilter(RomSet.current.type.exts));
			
			ScannerWorker worker = new ScannerWorker(foundFiles);
			worker.execute();
		}
		catch (NullPointerException e)
		{
			Main.logln("[ERROR] Roms path doesn't exist! Scanning will halt.");
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
	    ProgressDialog.init(Main.mainFrame, "Rom Scan");
	    
	    for (int i = 0; i < total; ++i)
	    {
	      setProgress((int)((((float)i)/total)*100));
	      
	      /*try {
	      Thread.sleep(200);
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	      }*/
	      
	      File f = files.get(i);
	      scanFile(f);
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
