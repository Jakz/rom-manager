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
    File romsPath = new File(Settings.current().romsPath);
    
    for (File file : files)
    {
      if (file.isFile())
      {
      
        ScanResult result = Main.scanner.scanFile(file);
      
        if (result != null)
        {
          // a missing rom has been dropped on list
          if (result.rom.status == RomStatus.NOT_FOUND)
          {
            result.rom.file = result.entry;
            Rom rom = result.rom;
            
            // first let's copy the file in the rompath
            File romFile = rom.file.file();
            if (!romFile.getParentFile().equals(romsPath))
            {
              File destFile = new File(romsPath, romFile.getName());
              romFile.renameTo(destFile);
              rom.file = rom.file.build(destFile);
            }
            
            // rename it if needed
            if (Settings.current().useRenamer && !Renamer.isCorrectlyNamed(rom.file.plainName(), rom))
              Renamer.renameRom(rom);
            
            if (Settings.current().organizeByFolders)
              Renamer.organizeRom(rom, Settings.current().folderSize);
            
            rom.status = RomStatus.FOUND;
            
            ++Main.romList.countCorrect;
            --Main.romList.countNotFound;
            Main.mainFrame.updateTable();
          }
          
        }
      }
      
      
    }
  }
}
