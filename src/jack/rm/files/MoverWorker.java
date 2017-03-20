package jack.rm.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.data.romset.GameSet;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.folder.FolderPlugin;

public class MoverWorker extends RomSetWorker<FolderPlugin>
{
  private static final Logger logger = Log.getLogger(LogSource.ORGANIZER);
  
  public MoverWorker(GameSet romSet, FolderPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, r -> r.status != GameStatus.MISSING, callback);
  }

  @Override
  public void execute(Game rom)
  {
    try
    {      
      Path finalPath = romSet.getSettings().romsPath.resolve(operation.getFolderForRom(rom));

      if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
      {
        Files.createDirectories(finalPath);
        logger.i(LogTarget.none(), "Creating folder "+finalPath);
      }
      
      Path currentFile = rom.getHandle().path();
      Path newFile = finalPath.resolve(currentFile.getFileName());
              
      if (!newFile.equals(currentFile) && Files.exists(newFile))
      {
        
        logger.e(LogTarget.rom(rom), "Cannot rename to "+newFile.toString()+", file exists");
      }
      else if (!newFile.equals(currentFile))
      {  
        rom.move(newFile);
        logger.i(LogTarget.rom(rom), "Moved rom to "+finalPath);
      }
      
      rom.updateStatus();
    }
    catch (Exception e)
    {
      //TODO: handle and log
      e.printStackTrace();
    }   
  }

}
