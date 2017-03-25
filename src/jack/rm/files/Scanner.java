package jack.rm.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.concurrent.AsyncGuiPoolWorker;
import com.pixbits.lib.concurrent.Operation;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.io.archive.HandleSet;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import jack.rm.Main;
import jack.rm.gui.Dialogs;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.scanners.ScannerPlugin;
import jack.rm.plugins.scanners.VerifierPlugin;
import net.sf.sevenzipjbinding.SevenZip;

public class Scanner
{
	private static final Logger logger = Log.getLogger(LogSource.SCANNER);
  
  GameSet set;
	
	private Set<Path> ignoredPaths = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	ScannerPlugin scanner;
	VerifierPlugin verifier;
		
  public Scanner(GameSet set)
	{
	  scanner = set.getSettings().plugins.getEnabledPlugin(PluginRealType.SCANNER);
	  verifier = set.getSettings().plugins.getEnabledPlugin(PluginRealType.VERIFIER);
	  
	  if (verifier != null)
	    verifier.setup(set);
	  
	  this.set = set;
	}

	public void foundRom(ScanResult result)
	{
	  if (result == null)
	    return;
	  	  
	  Rom rom = result.rom;
	  Game game = rom.game();
	  
	  logger.i(LogTarget.rom(result.rom), "Found a match for "+result.path);
	  
	  if (rom.isPresent() && !rom.handle().equals(result.path))
	  {	    
	    clones.add(result);
	    logger.w(LogTarget.file(result.path.path().getFileName()), "File contains a rom already present in romset: "+rom.handle());
	    return;
	  }
	  
	  result.assign();
	  game.updateStatus();
	}
	
	
	private boolean canProceedWithScan()
	{
	  Path folder = set.getSettings().romsPath;
 
	  if (scanner == null)
    {
      logger.e(LogTarget.romset(set), "Scanner plugin not enabled for romset");
      Dialogs.showError("Scanner Plugin", "No scanner plugin is enabled for the current romset.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.updateTable();
      return false;
    }
    else if (verifier == null)
    {
      logger.e(LogTarget.romset(set), "Verifier plugin not enabled for romset");
      Dialogs.showError("Verifier Plugin", "No verifier plugin is enabled for the current romset.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.updateTable();
      return false;
    }
    else if (folder == null || !Files.exists(folder))
    {
      logger.e(LogTarget.romset(set), "Roms path doesn't exist! Scanning interrupted");
      Dialogs.showError("Romset Path", "Romset path is not set, or it doesn't exists.\nPlease set one in Options.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.updateTable();
      return false;
    }
	  
	  return true;
	}
	
	public List<ScanResult> singleBlockingCheck(Path path)
	{
	  List<VerifierEntry> entries;
    try
    {
      entries = scanner.scanFile(path);
      return entries.stream()
      .map(entry -> verifier.verifyHandle(entry))
      .flatMap(r -> r.stream())
      .filter(s -> s.rom != null)
      .collect(Collectors.toList());
    } 
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
	}
	
	private Runnable verifyTask(List<VerifierEntry> entries, boolean total)
	{
	  return () -> {
	    logger.i(LogTarget.romset(set), "Verifying %s handles for romset", total ? "all" : "new");
	    
	    Operation<VerifierEntry, List<ScanResult>> operation = handle -> {
	      logger.d(LogTarget.romset(set), "> Verifying %s", handle.toString());
	      return verifier.verifyHandle(handle);
	    };
	    
	    BiConsumer<Long, Float> guiProgress = (i,f) -> {
	      Main.progress.update(f, "Verifying "+i+" of "+entries.size()+"...");
	      Main.mainFrame.updateTable();
	    };
	    
	    Consumer<List<ScanResult>> callback = results -> {
	      results.stream().filter(r -> r.rom != null).forEach(r -> foundRom(r));   
	      set.refreshStatus();
	    };
	    
	    Runnable onComplete = () -> {
	      SwingUtilities.invokeLater(() -> {
	        Main.progress.finished();
	        
	        if (!clones.isEmpty())
	          Main.clonesDialog.activate(set, clones);
	        else
	          set.saveStatus();
	      });
	    };
	    
	    AsyncGuiPoolWorker<VerifierEntry,List<ScanResult>> worker = new AsyncGuiPoolWorker<>(operation, guiProgress);
	    
	    SwingUtilities.invokeLater(() -> {
	      Main.progress.show(Main.mainFrame, "Verifying roms", () -> worker.cancel());
	    });

	    worker.compute(entries, callback, onComplete);
	  };
	}
		
	public void scanForRoms(boolean total) throws IOException
	{
		ignoredPaths.clear();
		foundFiles.clear();
		clones.clear();
		
		if (total)
		{
		  logger.i(LogTarget.romset(set), "Scanning for roms");
			set.resetStatus();
		}
		else
		{
		  logger.i(LogTarget.romset(set), "Scanning for new roms");

	    set.romStream()
	      .filter(r -> r.isPresent())
	      .map(r -> r.handle().path())
	      .forEach(ignoredPaths::add);
		}
		
		ignoredPaths.addAll(set.getSettings().getIgnoredPaths());

		Path folder = set.getSettings().romsPath;
		
		if (!canProceedWithScan())
		  return;
		
		FolderScanner folderScanner = new FolderScanner(ignoredPaths, true);
		final Set<Path> pathsToScan = folderScanner.scan(folder);
		List<VerifierEntry> foundEntries = Collections.synchronizedList(new ArrayList<>());
		
		/* scanning task */
		{
	    Operation<Path, List<VerifierEntry>> operation = path -> {
	      logger.d(LogTarget.file(path), "> Scanning %s", path.getFileName().toString());
	      try
        {
          return scanner.scanFile(path);
        } 
	      catch (IOException e)
        {
	        e.printStackTrace();
          return null;
        }
	      catch (RuntimeException e)
	      {
          logger.e(LogTarget.file(path), "Exception while scanning %s", path.toString());
	        e.printStackTrace();
	        if (!SevenZip.isInitializedSuccessfully())
          {
            Throwable ee = SevenZip.getLastInitializationException();
            ee.printStackTrace();
          }
          return null;
	      }
	    };
	    
	    BiConsumer<Long, Float> guiProgress = (i,f) -> {
	      Main.progress.update(f, "Scanning "+i+" of "+pathsToScan.size()+"...");
	    };
	    
	    Consumer<List<VerifierEntry>> callback = results -> {
	      foundEntries.addAll(results);
	    };
	    
	    Runnable onComplete = () -> {
	      SwingUtilities.invokeLater(() -> {
          Main.progress.finished();
          
          HandleSet handleSet = new HandleSet(foundEntries);
          logger.i(LogTarget.romset(set), "Found %d potential matches (%d binary, %d inside archives, %d nested inside %d archives).", 
              handleSet.totalHandles, handleSet.binaryCount, handleSet.archiveCount, handleSet.nestedArchiveInnerCount, handleSet.nestedArchiveCount);
          foundEntries.forEach(h -> logger.d(LogTarget.romset(set), "> %s", h.toString()));
          
          Runnable verifyTask = verifyTask(foundEntries, total);
          verifyTask.run();
	      });
	    };
	    
	    AsyncGuiPoolWorker<Path,List<VerifierEntry>> worker = new AsyncGuiPoolWorker<>(operation, guiProgress, 1);
      
      SwingUtilities.invokeLater(() -> {
        Main.progress.show(Main.mainFrame, "Scanning "+pathsToScan.size()+" files", () -> worker.cancel());
      });

      worker.compute(pathsToScan, callback, onComplete);
		}
	}
}
