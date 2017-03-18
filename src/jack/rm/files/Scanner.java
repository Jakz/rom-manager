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
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.swing.SwingWorker;

import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.io.archive.HandleSet;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.gui.Dialogs;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.scanners.ScannerPlugin;
import jack.rm.plugins.scanners.VerifierPlugin;

public class Scanner
{
	private static final Logger logger = Log.getLogger(LogSource.SCANNER);
  
  RomSet set;
	
	private Set<Path> existing = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	ScannerPlugin scanner;
	VerifierPlugin verifier;
		
  public Scanner(RomSet set)
	{
	  scanner = set.getSettings().plugins.getEnabledPlugin(PluginRealType.SCANNER);
	  verifier = set.getSettings().plugins.getEnabledPlugin(PluginRealType.VERIFIER);
	  this.set = set;
	}

	public void foundRom(ScanResult result)
	{
	  if (result == null)
	    return;
	  	  
	  Rom rom = result.rom;
	  
	  if (rom.status != RomStatus.MISSING)
	  {	    
	    clones.add(result);
	    logger.w(LogTarget.file(set.getSettings().romsPath.relativize(result.path.path())), "File contains a rom already present in romset: "+rom.getPath());
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
	  return null;
	}
		
	public void scanForRoms(boolean total) throws IOException
	{
		existing.clear();
		foundFiles.clear();
		clones.clear();
		
		if (total)
		{
		  logger.i(LogTarget.romset(set), "Scanning for roms");
			set.list.resetStatus();
		}
		else
		{
		  logger.i(LogTarget.romset(set), "Scanning for new roms");

	    set.list.stream()
	    .filter(r -> r.status != RomStatus.MISSING)
	    .map(r -> r.getPath().path())
	    .forEach(existing::add);
		}

		Path folder = set.getSettings().romsPath;
			
		if (scanner == null)
		{
		  logger.e(LogTarget.romset(set), "Scanner plugin not enabled for romset");
		  Dialogs.showError("Scanner Plugin", "No scanner plugin is enabled for the current romset.", Main.mainFrame);
		  set.list.resetStatus();
		  Main.mainFrame.updateTable();
		  return;
		}
		else if (verifier == null)
		{
      logger.e(LogTarget.romset(set), "Verifier plugin not enabled for romset");
      Dialogs.showError("Verifier Plugin", "No verifier plugin is enabled for the current romset.", Main.mainFrame);
      set.list.resetStatus();
      Main.mainFrame.updateTable();
      return;
		}
		else if (folder == null || !Files.exists(folder))
		{
		  logger.e(LogTarget.romset(set), "Roms path doesn't exist! Scanning interrupted");
		  Dialogs.showError("Romset Path", "Romset path is not set, or it doesn't exists.\nPlease set one in Options.", Main.mainFrame);
		  set.list.resetStatus();
		  Main.mainFrame.updateTable();
		  return;
		}
			
		HandleSet handleSet = scanner.scanFiles(folder, set.getSettings().getIgnoredPaths());
		verifier.setup(set);
		
		logger.i(LogTarget.romset(set), "Found %d potential entries", handleSet.total());
			
		ScannerWorker worker = new ScannerWorker(handleSet);
		worker.execute();
	}

	public class ScannerWorker extends SwingWorker<Void, Integer>
	{
	  private final long total;
	  private final long simpleTotal;
	  
	  private final HandleSet handles;

	  ScannerWorker(HandleSet handles)
	  {
	    this.handles = handles;
	    //TODO maybe manage an unified iteration
	    simpleTotal = handles.binaryCount()+handles.archivedCount();
	    total = simpleTotal + handles.nestedCount();
	    
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

	      if (i < handles.binaryCount())
	      {
	        ScanResult result = verifier.verifyHandle(handles.binaries.get(i));
	        if (result.rom != null)
	          foundRom(result);         
	      }
	      else if (i < simpleTotal)
	      {
	        ScanResult result = verifier.verifyHandle(handles.archives.get(i - (int)handles.binaryCount()));
	        if (result.rom != null)
	          foundRom(result);         
	      }
	      else
	      {
	        List<ScanResult> results = verifier.verifyHandle(handles.nestedArchives.get(i - (int)simpleTotal));
	        results.stream().filter(result -> result.rom != null).forEach(result -> foundRom(result));
	      }

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
      	    
	    Main.progress.update(this, "Scanning "+v.get(v.size()-1)+" of "+total+"..");
	    Main.mainFrame.updateTable();
	  }
	  
	  @Override
	  public void done()
	  {
	    try
      {
        get();
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      } catch (ExecutionException e)
      {
        e.printStackTrace();
      }
	    
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
