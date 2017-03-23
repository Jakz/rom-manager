package jack.rm.files;

import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.plugins.renamer.RenamerPlugin;

public class RenamerWorker extends RomSetWorker<RenamerPlugin>
{
  public RenamerWorker(GameSet romSet, RenamerPlugin plugin, Consumer<Boolean> callback)
  {
    super(romSet, plugin, r -> r.getStatus() == GameStatus.UNORGANIZED, callback);
  }

  @Override
  public void execute(Game rom)
  {
    if (rom.getStatus() == GameStatus.UNORGANIZED)
    {        
      Organizer.renameRom(rom);  
      
      if (romSet.getSettings().shouldRenameInternalName)
        Organizer.internalRenameRom(rom);
      
      rom.updateStatus();
      romSet.refreshStatus();
    }
  }

}
