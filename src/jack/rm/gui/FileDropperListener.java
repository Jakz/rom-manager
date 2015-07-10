package jack.rm.gui;

import net.iharder.dnd.*;
import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.*;

import java.io.File;

public class FileDropperListener implements FileDrop.Listener
{
  public void filesDropped(File[] files)
  {
    for (File file : files)
    {
      ScanResult result = Main.scanner.scanFile(file);
      
      if (result != null)
      {
        // a missing rom has been dropped on list
        if (result.rom.status == RomStatus.FOUND)
        {

        }
      }
      
      
    }
  }
}
