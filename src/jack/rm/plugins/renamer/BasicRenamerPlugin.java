package jack.rm.plugins.renamer;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

public class BasicRenamerPlugin extends RenamerPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Basic Renamer", new PluginVersion(1,0), "Jack",
        "This plugins renaming of roms by using rom title.");
  }
  
  @Override public String getNameForGame(Game rom)
  {
    return rom.getTitle();
  }
  
  @Override public String getCorrectInternalName(Game rom)
  {
    return getNameForGame(rom);
  }
  
  @Override public boolean isNative() { return true; }
}
