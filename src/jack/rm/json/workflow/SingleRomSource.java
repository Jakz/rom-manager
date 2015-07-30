package jack.rm.json.workflow;

import java.util.function.Consumer;

import com.pixbits.workflow.*;

import jack.rm.data.Rom;

public class SingleRomSource extends Fetcher<RomHandle>
{
  private final Rom rom;
  private boolean done;
  
  public SingleRomSource(Rom rom)
  {
    super(1);
    this.rom = rom;
    this.done = false;
  }
  
  @Override public boolean tryAdvance(Consumer<? super RomHandle> operation)
  {
    if (!done)
    {
      operation.accept(new RomHandle(rom));
      done = true;
      return true;
    }
    else
      return false;
  }
}
