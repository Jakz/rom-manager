package jack.rm.plugins;

import java.util.function.Predicate;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.plugin.PluginBuilder;

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
