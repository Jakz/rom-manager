package jack.rm.workflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.workflow.Dumper;

public class RomConsolidator extends Dumper<RomWorkflowEntry>
{
  Path destination;
  boolean overwrite;

  
  public RomConsolidator(Path path)
  {
    this.destination = path;
    this.overwrite = false;
  }
  
  public void accept(RomWorkflowEntry handle)
  {
    Game game = handle.getGame();
    
    //TODO: broken with new management
    game.stream().forEach(rom -> { 
      try
      {
        Path basePath = destination.resolve(handle.getDestPath());
        Path finalPath = basePath.resolve(game.getTitle()+"."+game.getSystem().exts[0]);
        
        if (!overwrite && Files.exists(finalPath))
          return;
        
        Files.createDirectories(basePath);
        
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
