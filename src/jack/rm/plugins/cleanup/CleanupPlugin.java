package jack.rm.plugins.cleanup;

import com.pixbits.plugin.PluginType;

import jack.rm.data.RomList;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class CleanupPlugin extends ActualPlugin
{
  public abstract void execute(RomList list);
  
  @Override public PluginType getPluginType() { return PluginRealType.ROMSET_CLEANUP; }
}
