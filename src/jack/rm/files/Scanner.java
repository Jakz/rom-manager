package jack.rm.files;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.swing.SwingWorker;

import com.pixbits.lib.ui.elements.ProgressDialog;
import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.gui.Dialogs;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.scanners.ScannerPlugin;

public class Scanner
{
	RomSet set;
	
	private Set<Path> existing = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	List<ScannerPlugin> scanners = new ArrayList<>();
	List<PathMatcher> matchers = new ArrayList<>();
		
	@SuppressWarnings("unchecked")
  public Scanner(PluginManager<ActualPlugin, ActualPluginBuilder> manager, RomSet set)
	{
	  Set<ActualPluginBuilder> parsers = manager.getBuildersByType(PluginRealType.SCANNER);
	  scanners = parsers.stream().map(b -> (ScannerPlugin)manager.build((Class<ScannerPlugin>)b.getID().getType())).collect(Collectors.toList());
	  Collections.sort(scanners);
	  
	  scanners.stream().map(ScannerPlugin::getHandledExtensions).forEachOrdered(s -> {
	    if (s != null)
	    {
	      String smatcher = Arrays.stream(s).collect(Collectors.joining(",", "glob:*.{", "}"));
	      matchers.add(FileSystems.getDefault().getPathMatcher(smatcher));
	    }
	    else
	      matchers.add(null);
	  });
	  
	  this.set = set;
	}
	
	public static long computeCRC(Path file)
	{
		try (CheckedInputStream cis = new CheckedInputStream(new BufferedInputStream(Files.newInputStream(file)), new CRC32()))
		{
			byte[] buf = new byte[1024];
			
			while (cis.read(buf) >= 0);
			
			long crc = cis.getChecksum().getValue();
			
			return crc;
		}
		catch (ClosedByInterruptException e)
		{
		  // thrown when cancelling a BackgroundOperation with the stream opened
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
	  
	  if (!existing.contains(file))
	  {
	    Iterator<ScannerPlugin> it = scanners.iterator();
	    Iterator<PathMatcher> pit = matchers.iterator();
	    while (result == null && it.hasNext())
	    {
	      ScannerPlugin scanner = it.next();
	      PathMatcher matcher = pit.next();
	      
	      if (matcher != null && matcher.matches(file.getFileName()))
	        result = scanner.scanRom(set.list, file);
	    }
	  }
	  
	  return result;
	  /*
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
            
            Rom rom = set.list.getByCRC32(curCrc);
            
            if (rom != null)
              result = new ScanResult(rom, RomPath.build(RomPath.Type.ZIP, file, entry.getName()));
          }
        }
        catch (Exception e)
        {
          Log.log(LogType.ERROR, LogSource.SCANNER, LogTarget.file(file), "Zipped file is corrupt, skipping");
        }
      }
    }
    else
    {
      if (!existing.contains(file))
      {    
        long crc = computeCRC(file);
        
        Rom rom = set.list.getByCRC32(crc);
        
        if (rom != null)
          return new ScanResult(rom, new BinaryHandle(file));
        else return null;
      }
    }
	  
	  return result;*/
	}
	
	protected PathMatcher buildPathMatcher()
	{
    Stream<String> stream = Arrays.stream(set.system.exts);
    
    final AtomicReference<Stream<String>> astream = new AtomicReference<Stream<String>>(stream); 
        
    scanners.stream().map(ScannerPlugin::getHandledExtensions).filter(i -> i != null).forEach(e -> {
      astream.set(Stream.concat(astream.get(), Arrays.stream(e)));
    });

    String pattern = astream.get().collect(Collectors.joining(",", "glob:*.{", "}"));
        
    return FileSystems.getDefault().getPathMatcher(pattern);
	}
	
	public void scanForRoms(boolean total) throws IOException
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
			
		foundFiles = new FolderScanner(buildPathMatcher(), set.getSettings().getIgnoredPaths(), true).scan(folder);

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
	    Main.progress.show(Main.mainFrame, "Rom Scan", () -> cancel(true) );
	    
	    for (int i = 0; i < total; ++i)
	    {
	      if (isCancelled())
	        return null;

	      int progress = (int)((((float)i)/total)*100);
	      setProgress(progress);

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
      	    
	    Main.progress.update(this, "Scanning "+v.get(v.size()-1)+" of "+foundFiles.size()+"..");
	    Main.mainFrame.updateTable();
	  }
	  
	  @Override
	  public void done()
	  {
	    if (isCancelled())
	      return;

	    
	    Main.progress.finished();
	    
	    if (!clones.isEmpty())
	      Main.clonesDialog.activate(set, clones);
	    else
	      set.saveStatus();
	  }
	  
	}
}
