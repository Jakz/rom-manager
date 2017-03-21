package jack.rm.files;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.jakz.romlib.data.game.Game;

import jack.rm.data.romset.GameSet;

public abstract class RomSetWorker<T extends BackgroundOperation> extends BackgroundWorker<Game, T>
{
  protected final GameSet romSet;
  
  public RomSetWorker(GameSet set, T plugin, Predicate<Game> filter, Consumer<Boolean> callback)
  {
    super(plugin, callback);
    set.stream().filter(filter).forEach(r -> this.add(r));
    this.romSet = set;

  }
  
  @Override
  public abstract void execute(Game rom);

}
