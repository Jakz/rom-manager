package jack.rm.plugins.folder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

public class AlphabeticalOrganizer extends FolderPlugin
{
  @Override
  public Path getFolderForGame(Game game)
  {
    String normalizedName = Normalizer.normalize(game.getTitle(), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return Paths.get(normalizedName.toUpperCase().charAt(0)+java.io.File.separator);
  }
    
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Alphabetical Organizer", new PluginVersion(1,0), "Jack",
        "This plugin organizes ROMs by folders named with the first letter of the rom title");
  }
}
