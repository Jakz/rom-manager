package jack.rm.plugins.renamer;

import java.util.function.Function;

import com.pixbits.plugin.PluginType;

import jack.rm.data.Rom;
import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class RenamerPlugin extends OrganizerPlugin implements Function<Rom,String>
{
  @Override public PluginType getPluginType() { return PluginRealType.RENAMER; }
  
  public abstract String getCorrectName(Rom rom);
  public final String apply(Rom rom) { return getCorrectName(rom); }
}
