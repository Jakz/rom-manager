package jack.rm.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.SwingUtilities;
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
import com.pixbits.lib.io.archive.handles.HandleLink;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.data.romset.Settings;
import jack.rm.gui.Dialogs;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.types.FormatSupportPlugin;
import jack.rm.plugins.types.ScannerPlugin;
import jack.rm.plugins.types.VerifierPlugin;
import net.sf.sevenzipjbinding.SevenZip;

public class Scanner
{
	private static final Logger logger = Log.getLogger(LogSource.SCANNER);
  
  GameSet set;
  Settings settings;
	
	private Set<Path> ignoredPaths = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	ScannerPlugin scanner;
	// TODO: support for multiple verifiers?
	VerifierPlugin verifier;
	Set<FormatSupportPlugin> formats;
		
  public Scanner(GameSet set)
	{
    this.set = set;
    MyGameSetFeatures helper = set.helper();
    this.settings = helper.settings();
    
    scanner = settings.plugins.getEnabledPlugin(PluginRealType.SCANNER);
	  verifier = settings.plugins.getEnabledPlugin(PluginRealType.VERIFIER);
	  //TODO: sort according to priority
	  formats = settings.plugins.getEnabledPlugins(PluginRealType.FORMAT_SUPPORT);
	  
	  if (verifier != null)
	  {
	    verifier.setup(set);
      verifier.setEntryTransformer(getTransformer());
	  }
	}
  
  private Collection<Set<Rom>> computeSharedRoms(GameSet set)
  {
    Map<Rom.Hash, Set<Rom>> mapping = new HashMap<>();
    
    set.romStream().forEach(rom -> {
      mapping.compute(rom.hash(), (h,s) -> {
        if (s == null)
          s = new HashSet<>();

        s.add(rom);
        return s;
      });
      
    });
    
    return mapping.values()
        .stream()
        .filter(s -> s.size() > 1)
        .collect(Collectors.toList());
  }

	private void foundRom(ScanResult result)
	{
	  if (result == null)
	    return;
	  	  
	  Rom rom = result.rom;
	  Game game = rom.game();
	  
	  logger.i(LogTarget.rom(result.rom), "Found a match for "+result.handle);
	  
	  if (rom.isPresent() && !rom.handle().equals(result.handle))
	  {	    
	    clones.add(result);
	    logger.w(LogTarget.file(result.handle.path().getFileName()), "File contains a rom already present in romset: "+rom.handle());
	    return;
	  }
	  
	  result.assign();
	  game.updateStatus();
	}
	
	
	private boolean canProceedWithScan()
	{
	  Path folder = settings.romsPath;
 
	  if (scanner == null)
    {
      logger.e(LogTarget.romset(set), "Scanner plugin not enabled for romset");
      Dialogs.showError("Scanner Plugin", "No scanner plugin is enabled for the current romset.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.rebuildGameList();
      return false;
    }
    else if (verifier == null)
    {
      logger.e(LogTarget.romset(set), "Verifier plugin not enabled for romset");
      Dialogs.showError("Verifier Plugin", "No verifier plugin is enabled for the current romset.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.rebuildGameList();
      return false;
    }
    else if (folder == null || !Files.exists(folder))
    {
      logger.e(LogTarget.romset(set), "Roms path doesn't exist! Scanning interrupted");
      Dialogs.showError("Romset Path", "Romset path is not set, or it doesn't exists.\nPlease set one in Options.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.rebuildGameList();
      return false;
    }
	  
	  return true;
	}
	
	private Function<VerifierEntry, ? extends VerifierEntry> getTransformer()
	{
    if (formats.isEmpty())
      return null;
    else
    {
	  
  	    Function<VerifierEntry, Handle> transformer = formats.stream().reduce(
          h -> (Handle)h, 
          (f,p) -> f.andThen(ff -> p.getSpecializedEntry(ff)),
          Function::andThen
      );
  	  
      return transformer;
    }
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
        Main.mainFrame.rebuildGameList();
	    };
	    
	    Consumer<List<ScanResult>> callback = results -> {
	      results.stream().filter(r -> r.rom != null).forEach(r -> foundRom(r));   
	      set.refreshStatus();
	    };
	    
	    Runnable onComplete = () -> {
	      SwingUtilities.invokeLater(() -> {
	        Collection<Set<Rom>> sharedRoms = computeSharedRoms(set);
	        
	        if (!sharedRoms.isEmpty())
	        {
	          logger.i(LogTarget.romset(set), "Found %d ROMs which are shared between multiple entries", sharedRoms.size());
	          for (Set<Rom> set : sharedRoms)
	            logger.d(LogTarget.rom(set.iterator().next()), "> %s is shared between %d entries", set.iterator().next().name, set.size());
	          
	          for (Set<Rom> set : sharedRoms)
	          {
	            final Optional<Rom> assigned = set.stream().filter(Rom::isPresent).findAny();
	            
	            if (assigned.isPresent())
	              set.stream()
	                .filter(Rom::isMissing)
	                .forEach(rom -> foundRom(new ScanResult(rom, new HandleLink(assigned.get().handle()))));
	          }
	        }
  
	        Main.progress.finished();
	        Main.mainFrame.rebuildGameList();
	        
	        if (!clones.isEmpty())
	          Main.clonesDialog.activate(set, clones);
	        else
	          Main.setManager.saveSetStatus(set);
	      });
	    };
	    
	    AsyncGuiPoolWorker<VerifierEntry,List<ScanResult>> worker = new AsyncGuiPoolWorker<>(operation, guiProgress);
	    
      Runnable onCancel = () -> {
        logger.d(LogTarget.romset(set), "Verification halted by user");
        worker.cancel();
      };
	    
	    SwingUtilities.invokeLater(() -> {
	      Main.progress.show(Main.mainFrame, "Verifying roms", onCancel);
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
		
		ignoredPaths.addAll(settings.getIgnoredPaths());

		Path folder = settings.romsPath;
		
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
          
          List<VerifierEntry> transformedEntries = foundEntries.stream()
            .collect(Collectors.toList());
          
          HandleSet handleSet = new HandleSet(transformedEntries);
          HandleSet.Stats stats = handleSet.stats();

          
          
          logger.i(LogTarget.romset(set), "Found %d potential matches (%d binary, %d inside archives, %d nested inside %d archives).", 
              stats.totalHandles, stats.binaryCount, stats.archivedCount, stats.nestedArchiveInnerCount, stats.nestedArchiveCount);
          
          transformedEntries.forEach(h -> logger.d(LogTarget.romset(set), "> %s", h.toString()));
          
          Runnable verifyTask = verifyTask(transformedEntries, total);
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
