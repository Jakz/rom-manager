package jack.rm.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.pixbits.workflow.Fetcher;

import jack.rm.data.rom.Rom;

public class MultipleRomSource extends Fetcher<RomWorkflowEntry>
{
  private final List<Rom> roms;
  private int current;
  
  public MultipleRomSource(Rom... roms)
  {
    super(roms.length);
    this.roms = new ArrayList<>();
    this.roms.addAll(Arrays.asList(roms));
    this.current = 0;
  }
  
  public MultipleRomSource(List<Rom> roms)
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
