package jack.rm.plugin.cleanup;

import jack.rm.data.RomList;
import jack.rm.plugin.*;

public abstract class CleanupPlugin extends Plugin
{
  public abstract void execute(RomList list);
  
  @Override public PluginType getType() { return PluginRealType.ROMSET_CLEANUP; }
}
