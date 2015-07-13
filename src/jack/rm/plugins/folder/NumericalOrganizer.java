package jack.rm.plugins.folder;

import java.nio.file.Paths;

import java.nio.file.Path;

import jack.rm.data.Rom;
import jack.rm.data.NumberedRom;
import jack.rm.files.Organizer;
import jack.rm.plugin.ExposedParameter;

public class NumericalOrganizer extends FolderPlugin
{
  @ExposedParameter
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
}
