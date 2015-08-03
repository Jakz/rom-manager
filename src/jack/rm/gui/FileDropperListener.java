package jack.rm.gui;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.files.Organizer;
import jack.rm.files.ScanResult;
import jack.rm.log.*;

import java.nio.file.Files;
import java.nio.file.Path;

import com.pixbits.gui.FileTransferHandler;

public class FileDropperListener implements FileTransferHandler.Listener
{
  @Override
  public void filesDropped(Path[] files)
  {
    new Thread()
    {
      
    @Override
    public void run()
    {
      Path romsPath = RomSet.current.getSettings().romsPath;
      
      for (Path file : files)
      {
        
        if (Files.isRegularFile(file))
        {
          System.out.println("Processing "+file.getFileName());
  
          
          ScanResult result = Main.scanner.scanFile(file);
        
          if (result != null)
          {
            try
            {
            
            // a missing rom has been dropped on list
            if (result.rom.status == RomStatus.MISSING)
            {
              result.assign();
              Rom rom = result.rom;
              
              // first let's copy the file in the rompath
              Path romFile = rom.getPath().file();
              if (!romFile.getParent().equals(romsPath))
              {
                Path destFile = romsPath.resolve(romFile.getFileName());
                rom.move(destFile);
                
              }
              
              rom.status = RomStatus.FOUND;
              
              Organizer.organizeRomIfNeeded(rom);
              
              RomSet.current.list.updateStatus();
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
