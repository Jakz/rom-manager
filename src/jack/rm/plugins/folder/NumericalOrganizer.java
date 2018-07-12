package jack.rm.plugins.folder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.files.Organizer;

public class NumericalOrganizer extends FolderPlugin
{
  @ExposedParameter(name="Folder Size")
  private int folderSize = 100;
  
  public NumericalOrganizer()
  {
    folderSize = 100;
  }
    
  @Override 
  public Path getFolderForGame(Game game)
  {
    int number = game.getGameSet().doesSupportAttribute(GameAttribute.NUMBER) ? game.getAttribute(GameAttribute.NUMBER) : game.getAttribute(GameAttribute.ORDINAL);
    int which = (number - 1) / folderSize;
    String first = Organizer.formatNumber(folderSize*which+1);
    String last = Organizer.formatNumber(folderSize*(which+1));
    return Paths.get(first+"-"+last+java.io.File.separator);
  }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Numerical Organizer", new PluginVersion(1,0), "Jack",
        "This plugin organizes ROMs which have a number by splitting them into folders of a specified size.");
  }
  
  /*@Override
  public Predicate<GameSet> compatibility() { return rs -> rs.doesSupportAttribute(GameAttribute.NUMBER); }*/

}
