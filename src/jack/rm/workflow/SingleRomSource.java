package jack.rm.workflow;

import java.util.function.Consumer;

import com.pixbits.workflow.Fetcher;

import jack.rm.data.rom.Rom;

public class SingleRomSource extends Fetcher<RomWorkflowEntry>
{
  private final Rom rom;
  private boolean done;
  
  public SingleRomSource(Rom rom)
  {
    super(1);
    this.rom = rom;
    this.done = false;
  }
  
  @Override public boolean tryAdvance(Consumer<? super RomWorkflowEntry> operation)
  {
    if (!done)
    {
      operation.accept(new RomWorkflowEntry(rom));
      done = true;
      return true;
    }
    else
      return false;
  }
}
