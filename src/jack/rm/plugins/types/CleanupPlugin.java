package jack.rm.plugins.types;

import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.OperationalPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class CleanupPlugin extends ActualPlugin implements OperationalPlugin
{  
  @Override public PluginType<?> getPluginType() { return PluginRealType.ROMSET_CLEANUP; }
}
