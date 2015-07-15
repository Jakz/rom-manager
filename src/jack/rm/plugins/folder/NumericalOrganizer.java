package jack.rm.plugins.folder;

import java.nio.file.Paths;
import com.pixbits.plugin.ExposedParameter;
import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import java.nio.file.Path;

import jack.rm.data.Rom;
import jack.rm.data.NumberedRom;
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
  public Path getFolderForRom(Rom rom)
  {
    int which = (((NumberedRom)rom).number - 1) / folderSize;
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

}
