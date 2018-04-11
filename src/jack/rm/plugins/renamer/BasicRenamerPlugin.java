package jack.rm.plugins.renamer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.plugins.types.RenamerPlugin;

public class BasicRenamerPlugin extends RenamerPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Basic Renamer", new PluginVersion(1,0), "Jack",
        "This plugins renames games and roms according to DAT titles.");
  }
  
  @Override public String getNameForGame(Game game)
  {
    return game.getTitle();
  }
  
  @Override public String getNameForRom(Rom rom)
  {
    return rom.name;
  }
  
  @Override public boolean isNative() { return true; }
}
