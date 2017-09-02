package jack.rm.files;

import java.util.function.Consumer;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toList;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.concurrent.BackgroundWorker;
import com.pixbits.lib.concurrent.OperationDetails;

import jack.rm.Main;

public abstract class RomSetWorker<T extends OperationDetails> extends BackgroundWorker<Game, T>
{
  protected final GameSet romSet;
  
  public RomSetWorker(GameSet set, T plugin, Predicate<Game> filter, Consumer<Boolean> onComplete)
  {
    super(
        set.stream().filter(filter).collect(toList()), 
        plugin, 
        worker -> Main.progress.show(Main.mainFrame, worker.getTitle(), null),
        success -> { Main.progress.finished(); onComplete.accept(success); },
        (w, i, t) -> {
          Main.progress.update(w, w.getProgressText()+" "+i+" of "+t+"..");
          Main.mainFrame.rebuildGameList();
        }
    );
    this.romSet = set;

  }
  
  @Override
  public abstract void execute(Game rom);

}
