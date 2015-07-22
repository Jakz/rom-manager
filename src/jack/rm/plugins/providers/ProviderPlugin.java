package jack.rm.plugins.providers;

import com.pixbits.plugin.PluginType;

import jack.rm.data.console.System;
import jack.rm.data.set.RomSet;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class ProviderPlugin extends ActualPlugin
{
  public abstract RomSet<?> buildRomSet(System system);
  public abstract System[] getSupportedSystems(System system);
  
  public PluginType<?> getPluginType() { return PluginRealType.PROVIDER; }
}
