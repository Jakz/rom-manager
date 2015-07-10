package jack.rm.gui;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.*;
import jack.rm.log.*;

import java.io.File;
import java.nio.file.Files;

public class FileDropperListener implements FileTransferHandler.Listener
{
  public void filesDropped(File[] files)
  {
    new Thread()
    {
      
    public void run()
    {
    
      File romsPath = new File(Settings.current().romsPath);
      
      for (File file : files)
      {
        
        if (file.isFile())
        {
          System.out.println("Processing "+file.getName());
  
          
          ScanResult result = Main.scanner.scanFile(file);
        
          if (result != null)
          {
            try
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
  
                Files.move(romFile.toPath(), destFile.toPath());
                
                rom.file = rom.file.build(destFile);
              }
              
              rom.status = RomStatus.FOUND;
              
              // rename it if needed
              if (Settings.current().useRenamer && !Renamer.isCorrectlyNamed(rom.file.plainName(), rom))
                Renamer.renameRom(rom);
              
              if (Settings.current().organizeByFolders)
                Renamer.organizeRom(rom, Settings.current().folderSize);
              
              
              Main.romList.updateStatus();
              Main.mainFrame.updateTable();
              
              Log.log(LogType.MESSAGE, LogSource.IMPORTER, LogTarget.rom(result.rom), "Successfully imported new rom");
            }
            else
              Log.log(LogType.WARNING, LogSource.IMPORTER, LogTarget.rom(result.rom), "Imported file is a rom already included in romset, skipping import");
            }
            catch (Exception e)
            {
              // TODO: handle and write on log

              e.printStackTrace();
            }  
          }
          else
            Log.log(LogType.WARNING, LogSource.IMPORTER, LogTarget.file(file), "The file is not any recognized rom for this romset");
        }
      }
    }
    }.start();
  }
}
