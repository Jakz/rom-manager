package jack.rm.files;

import java.util.function.Consumer;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.GameStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.renamer.RenamerPlugin;

public class RenamerWorker extends RomSetWorker<RenamerPlugin>
{
  public RenamerWorker(RomSet romSet, RenamerPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, r -> r.status == GameStatus.UNORGANIZED, callback);
  }

  @Override
  public void execute(Rom rom)
  {
    if (rom.status == GameStatus.UNORGANIZED)
    {        
      Organizer.renameRom(rom);  
      
      if (romSet.getSettings().shouldRenameInternalName)
        Organizer.internalRenameRom(rom);
      
      rom.updateStatus();
      romSet.list.updateStatus();
    }
  }

}
