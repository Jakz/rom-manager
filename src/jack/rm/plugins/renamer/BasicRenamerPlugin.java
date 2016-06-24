package jack.rm.plugins.renamer;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.rom.Rom;

public class BasicRenamerPlugin extends RenamerPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Basic Renamer", new PluginVersion(1,0), "Jack",
        "This plugins renaming of roms by using rom title.");
  }
  
  @Override public String getCorrectName(Rom rom)
  {
    return rom.getTitle();
  }
  
  @Override public String getCorrectInternalName(Rom rom)
  {
    return getCorrectName(rom);
  }
  
  @Override public boolean isNative() { return true; }
}
