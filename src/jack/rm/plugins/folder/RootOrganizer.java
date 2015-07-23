package jack.rm.plugins.folder;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.Rom;

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
