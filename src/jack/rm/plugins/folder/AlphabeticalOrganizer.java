package jack.rm.plugins.folder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.rom.Rom;

public class AlphabeticalOrganizer extends FolderPlugin
{
  @Override
  public Path getFolderForRom(Rom rom)
  {
    String normalizedName = Normalizer.normalize(rom.getTitle(), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return Paths.get(normalizedName.toUpperCase().charAt(0)+java.io.File.separator);
  }
    
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Alphabetical Organizer", new PluginVersion(1,0), "Jack",
        "This plugin organizes ROMs by folders named with the first letter of the rom title");
  }
}
