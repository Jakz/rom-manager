package jack.rm.plugins.folder;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import jack.rm.data.Rom;
import jack.rm.plugin.PluginInfo;
import jack.rm.plugin.PluginVersion;

public class AlphabeticalOrganizer extends FolderPlugin
{
  @Override
  public Path getFolderForRom(Rom rom)
  {
    String normalizedName = Normalizer.normalize(rom.title, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return Paths.get(normalizedName.toUpperCase().charAt(0)+java.io.File.separator);
  }
  
  public PluginInfo getInfo()
  { 
    return new PluginInfo(getClass().getName(), new PluginVersion(1,0), "Jack",
        "This plugin organizes ROMs by folders named with the first letter of the rom title");
  }
}
