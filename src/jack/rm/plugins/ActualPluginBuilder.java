package jack.rm.plugins;

import java.util.function.Predicate;

import com.pixbits.lib.plugin.PluginBuilder;

import jack.rm.data.romset.GameSet;

public class ActualPluginBuilder extends PluginBuilder<ActualPlugin>
{
  Predicate<GameSet> compatibility;
  
  public ActualPluginBuilder(ActualPlugin plugin)
  {
    super(plugin);
    this.compatibility = plugin.compatibility();
  }
  
  public boolean isCompatible(GameSet romset) { return compatibility.test(romset); }
}
