package jack.rm.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.workflow.Fetcher;

public class MultipleRomSource extends Fetcher<RomWorkflowEntry>
{
  private final List<Game> roms;
  private int current;
  
  public MultipleRomSource(Game... roms)
  {
    super(roms.length);
    this.roms = new ArrayList<>();
    this.roms.addAll(Arrays.asList(roms));
    this.current = 0;
  }
  
  public MultipleRomSource(List<Game> roms)
  {
    super(roms.size());
    this.roms = new ArrayList<>(roms);
    this.current = 0;
  }
  
  @Override public boolean tryAdvance(Consumer<? super RomWorkflowEntry> operation)
  {
    if (current < roms.size())
    {
      operation.accept(new RomWorkflowEntry(roms.get(current++)));
      return true;
    }
    else
      return false;
  }
}
