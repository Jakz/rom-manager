package jack.rm.data;

import jack.rm.Main;
import jack.rm.data.set.RomSet;
import jack.rm.files.*;
import jack.rm.gui.Dialogs;
import jack.rm.log.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.*;
import javax.swing.SwingWorker;

import com.pixbits.gui.ProgressDialog;

public class Scanner
{
	RomSet set;
	
	private Set<Path> existing = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	private PathMatcher archiveMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{zip}");
	
	public Scanner(RomSet set)
	{
		this.set = set;
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
	  
	  if (rom.status != RomStatus.MISSING)
	  {	    
	    clones.add(result);
	    Log.warning(LogSource.SCANNER, LogTarget.file(set.getSettings().romsPath.relativize(result.path.file())), "File contains a rom already present in romset: "+rom.getPath());
	    return;
	  }
	  
	  result.assign();

	  if (rom.isOrganized())
	    rom.status = RomStatus.FOUND;
	  else
	    rom.status = RomStatus.UNORGANIZED;
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
            
            Rom rom = set.list.getByCRC(curCrc);
            
            if (rom != null)
              result = new ScanResult(rom, new RomPath.Archive(file, entry.getName()));
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
      
      Rom rom = set.list.getByCRC(crc);
      
      if (rom != null)
        return new ScanResult(rom, new RomPath.Bin(file));
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
			Log.log(LogType.MESSAGE, LogSource.SCANNER, LogTarget.romset(set), "Scanning for roms");
			set.list.resetStatus();
		}
		else
		{
	    Log.log(LogType.MESSAGE, LogSource.SCANNER, LogTarget.romset(set), "Scanning for new roms");

	    set.list.stream()
	    .filter(r -> r.status != RomStatus.MISSING)
	    .map(r -> r.getPath().file())
	    .forEach(existing::add);
		}

		Path folder = set.getSettings().romsPath;
			
		if (folder == null || !Files.exists(folder) || !Files.isDirectory(folder))
		{
		  Log.log(LogType.ERROR, LogSource.SCANNER, LogTarget.romset(set), "Roms path doesn't exist! Scanning interrupted");
		  Dialogs.showError("Romset Path", "Romset path is not set, or it doesn't exists as a folder.\nPlease set one in Options.", Main.mainFrame);
		  set.list.resetStatus();
		  Main.mainFrame.updateTable();
		  return;
		}
			
		foundFiles = new FolderScanner(set.getFileMatcher(), set.getSettings().getIgnoredPaths()).scan(folder);

		ScannerWorker worker = new ScannerWorker(foundFiles);
		worker.execute();
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
	      set.list.updateStatus();
	      
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

	    
	    ProgressDialog.finished();
	    
	    if (!clones.isEmpty())
	      Main.clonesDialog.activate(set, clones);
	    else
	      set.saveStatus();
	  }
	  
	}
}
