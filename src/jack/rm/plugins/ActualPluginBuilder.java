package jack.rm.plugins;

import java.util.function.Predicate;

import com.pixbits.plugin.PluginBuilder;

import jack.rm.data.set.RomSet;

public class ActualPluginBuilder extends PluginBuilder<ActualPlugin>
{
  Predicate<RomSet> compatibility;
  
  public ActualPluginBuilder(ActualPlugin plugin)
  {
    super(plugin);
    this.compatibility = plugin.compatibility();
  }
  
  public boolean isCompatible(RomSet romset) { return compatibility.test(romset); }
}
