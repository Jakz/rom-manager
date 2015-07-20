package jack.rm.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import jack.rm.data.Rom;
import jack.rm.data.RomList;
import jack.rm.data.RomStatus;
import jack.rm.data.set.RomSet;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

public class RenamerWorker extends OrganizerWorker<RenamerPlugin>
{
  public RenamerWorker(RomSet<?> romSet, RenamerPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, callback);
  }

  @Override
  public void execute(Rom rom)
  {
    if (rom.status == RomStatus.INCORRECT_NAME)
    {        
      Organizer.renameRom(rom);  
      romSet.list.updateStatus();
    }
  }

}
