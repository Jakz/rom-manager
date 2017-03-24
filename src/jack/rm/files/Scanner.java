package jack.rm.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.SwingWorker;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.concurrent.AsyncGuiPoolWorker;
import com.pixbits.lib.concurrent.Operation;
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

public class Scanner
{
	private static final Logger logger = Log.getLogger(LogSource.SCANNER);
  
  GameSet set;
	
	private Set<Handle> existing = new HashSet<>();
	private Set<Path> foundFiles = new HashSet<>();
	private Set<ScanResult> clones = new TreeSet<>();
	
	ScannerPlugin scanner;
	VerifierPlugin verifier;
		
  public Scanner(GameSet set)
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
	  Game game = rom.game();
	  
	  logger.i(LogTarget.rom(result.rom), "Found a match for "+rom.handle());
	  
	  if (rom.isPresent() && !rom.handle().equals(result.path))
	  {	    
	    clones.add(result);
	    logger.w(LogTarget.file(result.path.path().getFileName()), "File contains a rom already present in romset: "+rom.handle());
	    return;
	  }
	  
	  result.assign();
	  game.updateStatus();
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
			set.resetStatus();
		}
		else
		{
		  logger.i(LogTarget.romset(set), "Scanning for new roms");

	    set.romStream()
	      .filter(r -> r.isPresent())
	      .map(r -> r.handle())
	      .forEach(existing::add);
		}

		Path folder = set.getSettings().romsPath;
			
		if (scanner == null)
		{
		  logger.e(LogTarget.romset(set), "Scanner plugin not enabled for romset");
		  Dialogs.showError("Scanner Plugin", "No scanner plugin is enabled for the current romset.", Main.mainFrame);
		  set.resetStatus();
		  Main.mainFrame.updateTable();
		  return;
		}
		else if (verifier == null)
		{
      logger.e(LogTarget.romset(set), "Verifier plugin not enabled for romset");
      Dialogs.showError("Verifier Plugin", "No verifier plugin is enabled for the current romset.", Main.mainFrame);
      set.resetStatus();
      Main.mainFrame.updateTable();
      return;
		}
		else if (folder == null || !Files.exists(folder))
		{
		  logger.e(LogTarget.romset(set), "Roms path doesn't exist! Scanning interrupted");
		  Dialogs.showError("Romset Path", "Romset path is not set, or it doesn't exists.\nPlease set one in Options.", Main.mainFrame);
		  set.resetStatus();
		  Main.mainFrame.updateTable();
		  return;
		}
			
		HandleSet handleSet = scanner.scanFiles(folder, set.getSettings().getIgnoredPaths());
		verifier.setup(set);
		
		logger.i(LogTarget.romset(set), "Found %d potential entries (%d binaries, %d archived, %d nested in %d batches)", handleSet.total(), handleSet.binaryCount(), handleSet.archivedCount(), handleSet.nestedCount(), handleSet.nestedArchives.size());
		handleSet.stream().forEach(h -> logger.d(LogTarget.romset(set), "> %s", h.toString()));

		logger.i(LogTarget.romset(set), "Verifying %s handles for romset", total ? "all" : "new");
		
    final List<Handle> handles = handleSet.stream().collect(Collectors.toList());

		
    Operation<Handle, List<ScanResult>> operation = handle -> {
      logger.d(LogTarget.romset(set), "> Verifying %s", handle.toString());
      return verifier.verifyHandle(handle);
    };
    
    BiConsumer<Long, Float> guiProgress = (i,f) -> {
      Main.progress.update(f, "Verifying "+i+" of "+handles.size()+"...");
      Main.mainFrame.updateTable();
    };
    
    Consumer<List<ScanResult>> callback = results -> {
      results.stream().filter(r -> r.rom != null).forEach(r -> foundRom(r));   
      set.refreshStatus();
    };
    
    Runnable onComplete = () -> {
      Main.progress.finished();
      
      if (!clones.isEmpty())
        Main.clonesDialog.activate(set, clones);
      else
        set.saveStatus();
    };
    
    AsyncGuiPoolWorker<Handle,List<ScanResult>> worker = new AsyncGuiPoolWorker<>(operation, guiProgress);
    Main.progress.show(Main.mainFrame, "Verifying roms", () -> worker.cancel() );

    worker.compute(handles, callback, onComplete);
	}
	
	public class VerifierWorker extends SwingWorker<Void, Integer>
	{
	  private final long total;
	  private final long simpleTotal;
	  
	  private final HandleSet handles;

	  VerifierWorker(HandleSet handles)
	  {
	    this.handles = handles;
	    //TODO maybe manage an unified iteration
	    simpleTotal = handles.binaryCount()+handles.archivedCount();
	    total = simpleTotal + handles.nestedArchives.size();
	    
	  }

	  @Override
	  public Void doInBackground()
	  {
	    try
	    {
	    
	    Main.progress.show(Main.mainFrame, "Verifying roms", () -> cancel(true) );
	    
	    int i = 0;
	    for (VerifierEntry entry : handles)
	    {
	       if (isCancelled())
	          return null;

	       int progress = (int)((((float)i)/total)*100);
	       setProgress(progress);
	       
         logger.d(LogTarget.romset(set), "> Verifying %s", entry.toString());
         List<ScanResult> result = verifier.verifyHandle(entry);
         result.stream().filter(r -> r.rom != null).forEach(r -> foundRom(r));   
         
         set.refreshStatus();

         publish(i);
         ++i;
	    }
	    
	    } catch (NullPointerException e)
	    {
	      e.printStackTrace();
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
	    if (isCancelled())
	      return;
	    
	    try
      {
        get();
      } 
	    catch (InterruptedException e)
      {
        e.printStackTrace();
      } 
	    catch (ExecutionException e)
      {
        e.printStackTrace();
      }
	    
	    Main.progress.finished();
	    
	    if (!clones.isEmpty())
	      Main.clonesDialog.activate(set, clones);
	    else
	      set.saveStatus();
	  }
	  
	}
}
