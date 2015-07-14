package jack.rm.plugins.cleanup;

import jack.rm.data.RomList;
import jack.rm.plugin.*;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class CleanupPlugin extends ActualPlugin
{
  public abstract void execute(RomList list);
  
  @Override public PluginType getType() { return PluginRealType.ROMSET_CLEANUP; }
}
