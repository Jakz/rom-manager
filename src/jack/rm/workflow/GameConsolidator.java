package jack.rm.workflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.workflow.Dumper;

public class GameConsolidator extends Dumper<GameEntry>
{
  Path destination;
  boolean overwrite;

  
  public GameConsolidator(Path path)
  {
    this.destination = path;
    this.overwrite = false;
  }
  
  public void accept(GameEntry handle)
  {
    Game game = handle.getGame();
    
    //TODO: broken with new management
    game.stream().forEach(rom -> { 
      try
      {
        Path finalPath = handle.getFinalPath(destination);
        
        if (!overwrite && Files.exists(finalPath))
          return;
        
        Files.createDirectories(finalPath.getParent());
        
        if (handle.hasBeenModified())
        {
          Files.move(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
        }
        else
        {
          Handle path = rom.handle();
          
          if (!path.isArchive())
            Files.copy(path.path(), finalPath, StandardCopyOption.REPLACE_EXISTING);
          else
            Files.copy(path.getInputStream(), finalPath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        //if (handle.getBuffer() == null)
        //  handle.prepareBuffer();
        
  
        //Files.deleteIfExists(handle.getPath());
      }
      catch (Exception e)
      {
        System.out.println("Error on "+rom.handle().toString());
        if (e.getCause() != e)
          e.getCause().printStackTrace();
        else
          e.printStackTrace();
      }
    });
  }
}
