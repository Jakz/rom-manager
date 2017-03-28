package jack.rm.plugins.renamer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.organizers.GameRenamer;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class RenamerPlugin extends OrganizerPlugin implements GameRenamer
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.RENAMER; }
  
  @Override public String getTitle() { return "Renaming ROMs"; }
  @Override public String getProgressText() { return "Renaming"; }
  
  @Override public abstract String getNameForGame(Game rom);
  
  public abstract String getCorrectInternalName(Game rom);
}
