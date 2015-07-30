package jack.rm.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.folder.FolderPlugin;

public class MoverWorker extends OrganizerWorker<FolderPlugin>
{
  public MoverWorker(RomSet romSet, FolderPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, callback);
  }

  @Override
  public void execute(Rom rom)
  {
    if (rom.status != RomStatus.MISSING)
    {     
      try
      {      
        Path finalPath = romSet.getSettings().romsPath.resolve(plugin.getFolderForRom(rom));
  
        if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
        {
          Files.createDirectories(finalPath);
          Log.message(LogSource.ORGANIZER, LogTarget.none(), "Creating folder "+finalPath);
        }
        
        Path currentFile = rom.getPath().file();
        Path newFile = finalPath.resolve(currentFile.getFileName());
                
        if (!newFile.equals(currentFile) && Files.exists(newFile))
        {
          
          Log.error(LogSource.ORGANIZER, LogTarget.rom(rom), "Cannot rename to "+newFile.toString()+", file exists");
        }
        else if (!newFile.equals(currentFile))
        {  
          rom.move(newFile);
          Log.message(LogSource.ORGANIZER, LogTarget.rom(rom), "Moved rom to "+finalPath);
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

}
