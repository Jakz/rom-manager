package jack.rm.files;

import java.util.function.Consumer;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.renamer.RenamerPlugin;

public class RenamerWorker extends OrganizerWorker<RenamerPlugin>
{
  public RenamerWorker(RomSet romSet, RenamerPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, callback);
  }

  @Override
  public void execute(Rom rom)
  {
    if (rom.status == RomStatus.UNORGANIZED)
    {        
      Organizer.renameRom(rom);  
      rom.updateStatus();
      romSet.list.updateStatus();
    }
  }

}
