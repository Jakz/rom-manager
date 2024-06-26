package jack.rm.workflow;

import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.workflow.Fetcher;

public class SingleRomSource extends Fetcher<GameEntry>
{
  private final Game rom;
  private boolean done;
  
  public SingleRomSource(Game rom)
  {
    super(1);
    this.rom = rom;
    this.done = false;
  }
  
  @Override public boolean tryAdvance(Consumer<? super GameEntry> operation)
  {
    if (!done)
    {
      operation.accept(new GameEntry(rom));
      done = true;
      return true;
    }
    else
      return false;
  }
}
