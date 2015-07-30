package jack.rm.workflow;

import java.util.HashSet;
import java.util.Set;

import com.pixbits.workflow.Mutuator;
import jack.rm.data.console.System;
import jack.rm.data.rom.Rom;

public abstract class RomOperation implements Mutuator<RomHandle>
{  
  public static enum Mode
  {
    EXCLUDE,
    INCLUDE
  };
  
  private final Set<Rom> specifics;
  private Mode mode;
  
  RomOperation()
  {
    specifics = new HashSet<Rom>();
    mode = Mode.EXCLUDE;
  }
  
  public void exclude(Rom rom)
  {
    if (mode == Mode.EXCLUDE)
      specifics.add(rom);
    else
      throw new UnsupportedOperationException("Can't exclude a ROM from an operation which is in INCLUDE mode");
  }
  
  public void include(Rom rom)
  {
    if (mode == Mode.INCLUDE || specifics.isEmpty())
    {
      mode = Mode.INCLUDE;
      specifics.add(rom);
    }
    else
      throw new UnsupportedOperationException("Can't include a ROM from an operation which is in EXCLUDE mode");
  }
  
  public boolean shouldBeProcessed(Rom rom)
  {
    return !(mode == Mode.INCLUDE ^ specifics.contains(rom));
  }
  
  abstract String getName();
  abstract String getDescription();
  boolean isSystemSupported(System system) { return true; }
  
  public final RomHandle apply(RomHandle handle)
  {
    return shouldBeProcessed(handle.getRom()) ? doApply(handle) : handle;
  }
  
  abstract protected RomHandle doApply(RomHandle handle);
}
