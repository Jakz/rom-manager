package jack.rm.plugins.folder;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.data.rom.Rom;

public class RootOrganizer extends FolderPlugin
{
  @Override
  public Path getFolderForRom(Rom rom)
  {
    return Paths.get(".");
  }
    
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Root Organizer", new PluginVersion(1,0), "Jack",
        "This plugin moves all ROMs in root path of the romset.");
  }
}
