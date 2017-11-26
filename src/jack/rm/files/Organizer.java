package jack.rm.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.GameSetFeatures;
import com.pixbits.lib.io.archive.handles.Handle;
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
	  if (!game.hasCorrectName())
	  {
	    renameRom(game);
	    internalRenameRom(game);
	  }
	  
	  if (!game.hasCorrectName())
	  
	  if (!game.hasCorrectFolder())
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
	
	public void renameRom(Game rom)
	{
    throw new UnsupportedOperationException("relocate name is not compatible with new handles");

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
          game.move(newFile);
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
}
