package jack.rm.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.folder.FolderPlugin;

public class MoverWorker extends RomSetWorker<FolderPlugin>
{
  private static final Logger logger = Log.getLogger(LogSource.ORGANIZER);
  
  public MoverWorker(RomSet romSet, FolderPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, r -> r.status != RomStatus.MISSING, callback);
  }

  @Override
  public void execute(Rom rom)
  {
    try
    {      
      Path finalPath = romSet.getSettings().romsPath.resolve(operation.getFolderForRom(rom));

      if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
      {
        Files.createDirectories(finalPath);
        logger.i(LogTarget.none(), "Creating folder "+finalPath);
      }
      
      Path currentFile = rom.getPath().file();
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
