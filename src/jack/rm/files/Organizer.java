package jack.rm.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;

import java.io.File;
import java.io.IOException;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.GameSetFeatures;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.HandleLink;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.data.romset.Settings;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.types.CleanupPlugin;
import jack.rm.plugins.types.PatternSetPlugin;
import jack.rm.plugins.types.RenamerPlugin;

public class Organizer
{	
	private static final Logger logger = Log.getLogger(LogSource.ORGANIZER);
  private static DecimalFormat format;
  
  public static String formatNumber(int index)
  {
    return format.format(index);
  }
  
	static
	{
		format = new DecimalFormat();
		format.applyPattern("0000");
	}
	
	private final GameSet set;
  private final MyGameSetFeatures helper;
  //private final Settings settings;

	private Settings settings() { return helper.settings(); }
	
	public Organizer(GameSet set, MyGameSetFeatures helper)
	{
	  this.set = set;
	  this.helper = helper;
	}

	public void organizeRomIfNeeded(Game game)
	{	  
	  if (!hasCorrectName(game))
	  {
	    renameRom(game);
	    internalRenameRom(game);
	  }
	  
	  //if (!hasCorrectName(game))
	  
	  if (!hasCorrectFolder(game))
	    moveRom(game);
	}

	public void internalRenameRom(Game rom)
	{
    throw new UnsupportedOperationException("relocate internal name is not compatible with new handles");

	  /* TODO
	  if (!rom.hasCorrectInternalName())
	  {
	    Handle path = rom.getHandle();
	    String name = rom.getCorrectInternalName() + "." + path.getInternalExtension();
	    
	    if (true)
	      throw new UnsupportedOperationException("relocate internal name is not compatible with new handles");
	    
	    if (((ArchiveHandle)path).renameInternalFile(name))
	      rom.setHandle(path.relocateInternal(name));
	    else
	      logger.e(LogTarget.game(rom), "Can't rename internal name of archive: "+path.path());
	  }*/
	}
	
	public void renameRom(Game game) 
	{
    /* special case: all roms of the game are inside same archive, just rename the archive */
	  Set<Path> filesForGame = game.stream().map(Rom::handle).map(Handle::path).collect(Collectors.toSet());
	  if (filesForGame.size() == 1 && !game.stream().map(Rom::handle).anyMatch(h -> h instanceof HandleLink))
	  {
	    Path path = filesForGame.iterator().next();
	    String correctName = game.getCorrectName() + '.' + FileUtils.pathExtension(path);
	    Path correctPath = path.getParent().resolve(correctName);
	    
	    if (!path.equals(correctPath))
	    {	    
  	    //TODO: forward exception to caller
  	    try
  	    {
  	      Files.move(path, correctPath);
  	      game.stream().map(Rom::handle).forEach(handle -> handle.relocate(correctPath));
  	    }
  	    catch (IOException exception)
  	    {
  	      exception.printStackTrace();
  	    }
	    }
	  }
	  
	  
	  //throw new UnsupportedOperationException("relocate name is not compatible with new handles");

	  /* TODO

	  Handle romPath = rom.getHandle();
	  Path renameTo = romPath.path().getParent();
	  
	  //TODO: should fix extensions if wrong and crc is verified but now just keeps them
    renameTo = renameTo.resolve(rom.getCorrectName()+"."+romPath.getExtension());

    try
    {
      rom.move(renameTo);
    }
    catch (FileAlreadyExistsException e)
    {
      logger.e(LogTarget.game(rom), "Can't rename file, already exists: "+e.getFile());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      // TODO: handle and write on log
    }*/
	}
	
	public void moveRom(Game game)
	{
	  if (game.getStatus().isComplete())
    {     
      try
      {      
        Path finalPath = settings().romsPath.resolve(game.getCorrectFolder());
  
        if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
        {
          Files.createDirectories(finalPath);
          logger.i(LogTarget.none(), "Creating folder "+finalPath);
        }
        
        Handle romPath = game.rom().handle();
        Path newFile = finalPath.resolve(romPath.path().getFileName());
                
        if (!newFile.equals(romPath.path()) && Files.exists(newFile))
        {
          logger.e(LogTarget.game(game), "Cannot rename to "+newFile.toString()+", file exists");
        }
        else if (!newFile.equals(romPath.path()))
        {  
          //game.move(newFile); TODO
          logger.e(LogTarget.game(game), "Moved rom to "+finalPath);
        }    
      }
      catch (Exception e)
      {
        //TODO: handle and log
        e.printStackTrace();
      }   
    } 
	}
	
	public void organize()
	{
	  RenamerPlugin renamer = settings().getRenamer();
    FolderPlugin organizerPlugin = settings().getFolderOrganizer();
    boolean hasCleanupPhase = settings().hasCleanupPlugins();
    
    Consumer<Boolean> cleanupPhase = b -> { cleanup(); Main.setManager.saveSetStatus(set); };
    Consumer<Boolean> moverPhase = organizerPlugin == null ? cleanupPhase : b -> new MoverWorker(set, organizerPlugin, cleanupPhase).execute();
    Consumer<Boolean> renamerPhase = renamer == null ? moverPhase : b -> new RenamerWorker(set, renamer, moverPhase).execute();

    renamerPhase.accept(true);
	}
	
  public void cleanup()
  {
    MyGameSetFeatures helper = set.helper();
    Settings settings = helper.settings();
    Set<CleanupPlugin> plugins = settings.plugins.getEnabledPlugins(PluginRealType.ROMSET_CLEANUP);
    plugins.stream().forEach( p -> p.execute(set) );
  }
  
  private boolean hasCorrectName(Game game)
  {
    throw new UnsupportedOperationException("Must be reimplemented");

    /* TODO
    
    Settings settings = set.getSettings();
    
    boolean hasCorrectName = getCorrectName().equals(handle.plainName());
    
    if (!settings.shouldRenameInternalName)
      return hasCorrectName;
    else
      return hasCorrectName && hasCorrectInternalName();
      */
  }
  
  public boolean hasCorrectFolder(Game game)
  {
    throw new UnsupportedOperationException("Must be reimplemented");

    /* TODO
    
    try {
      return set.getSettings().getFolderOrganizer() == null || 
        Files.isSameFile(handle.path().getParent(), set.getSettings().romsPath.resolve(getCorrectFolder()));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }*/
  }
  
  private boolean isOrganized(Game game)
  {
    //TODO: forced true because hasCorrectName and hasCorrectFolder throw exceptions forcibly
    if (true)
      return true;

    boolean name = hasCorrectName(game), folder = hasCorrectFolder(game);
    return true || (name && folder); 
  }
  
  public void computeStatus()
  {
    Map<Path, Set<Rom>> files = set.stream()
      .flatMap(Game::stream)
      .filter(Rom::isPresent)
      .collect(groupingBy(r -> r.handle().path(), mapping(k -> k, toSet())));
    
    logger.i(LogTarget.romset(set), "Computed file map for organizer: %d roms in %d files", files.values().stream().flatMap(Set::stream).count(), files.size());
    
    List<Map.Entry<Path, Set<Rom>>> sharedRoms = new ArrayList<>(), sharedGames = new ArrayList<>();
    
    for (Map.Entry<Path, Set<Rom>> entry : files.entrySet())
    {
      Set<Rom> roms = entry.getValue();
      final Game game = roms.iterator().next().game();
      
      if (roms.size() > 1) sharedRoms.add(entry);
      if (roms.stream().anyMatch(r -> r.game() != game)) sharedGames.add(entry);
    }
    
    logger.d(LogTarget.romset(set), "Multiple roms mapped to same path: %d, multiple games mapped to same path: %d", sharedRoms.size(), sharedGames.size());
    
    for (Map.Entry<Path, Set<Rom>> entry : sharedGames)
    { 
      logger.d(LogTarget.romset(set), "Path %s is shared by multiple games:", entry.getKey().toString());
      for (Rom rom : entry.getValue())
        logger.d(LogTarget.romset(set), " > %s (%s)", rom.name, rom.game().getTitle());
    }
  }

}
