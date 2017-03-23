package jack.rm.files;

import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.log.LogSource;
import jack.rm.plugins.folder.FolderPlugin;

public class MoverWorker extends RomSetWorker<FolderPlugin>
{
  private static final Logger logger = Log.getLogger(LogSource.ORGANIZER);
  
  public MoverWorker(GameSet romSet, FolderPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, r -> r.getStatus().isComplete(), callback);
  }

  @Override
  public void execute(Game game)
  {
    // TODO: rewrite for new management
    /*try
    {      
      Path finalPath = romSet.getSettings().romsPath.resolve(operation.getFolderForRom(game));

      if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
      {
        Files.createDirectories(finalPath);
        logger.i(LogTarget.none(), "Creating folder "+finalPath);
      }
      
      Path currentFile = game.getHandle().path();
      Path newFile = finalPath.resolve(currentFile.getFileName());
              
      if (!newFile.equals(currentFile) && Files.exists(newFile))
      {
        
        logger.e(LogTarget.game(game), "Cannot rename to "+newFile.toString()+", file exists");
      }
      else if (!newFile.equals(currentFile))
      {  
        game.move(newFile);
        logger.i(LogTarget.game(game), "Moved rom to "+finalPath);
      }
      
      game.updateStatus();
    }
    catch (Exception e)
    {
      //TODO: handle and log
      e.printStackTrace();
    } */  
  }

}
