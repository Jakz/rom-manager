package jack.rm.files;

import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.plugins.types.RenamerPlugin;

public class RenamerWorker extends RomSetWorker<RenamerPlugin>
{
  final Organizer organizer;
  
  public RenamerWorker(GameSet set, RenamerPlugin plugin, Consumer<Boolean> callback)
  {
    super(set, plugin, r -> r.getStatus() == GameStatus.UNORGANIZED, callback);
    MyGameSetFeatures helper = set.helper();
    organizer = helper.organizer();
  }

  @Override
  public void execute(Game game)
  {
    if (game.getStatus() == GameStatus.UNORGANIZED)
    {        
      organizer.renameRom(game);  
      
      if (Main.setManager.settings(romSet).shouldRenameInternalName)
        organizer.internalRenameRom(game);
      
      game.updateStatus();
      romSet.refreshStatus();
    }
  }

}
