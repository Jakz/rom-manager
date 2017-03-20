package jack.rm.plugins.renamer;

import java.util.function.Function;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class RenamerPlugin extends OrganizerPlugin implements Function<Game,String>
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.RENAMER; }
  
  @Override public String getTitle() { return "Renaming ROMs"; }
  @Override public String getProgressText() { return "Renaming"; }
  
  public abstract String getCorrectName(Game rom);
  public final String apply(Game rom) { return getCorrectName(rom); }
  
  public abstract String getCorrectInternalName(Game rom);
}
