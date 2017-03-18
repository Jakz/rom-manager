package jack.rm.gui;

import java.nio.file.Files;
import java.nio.file.Path;

import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.romset.RomSet;
import jack.rm.files.Organizer;
import jack.rm.files.ScanResult;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public class FileDropperListener implements FileTransferHandler.Listener
{
  private static final Logger logger = Log.getLogger(LogSource.IMPORTER);
  
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
  
          
          ScanResult result = RomSet.current.getScanner().scanFile(file);
        
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
              
              logger.i(LogTarget.rom(result.rom), "Successfully imported new rom");
            }
            else
              logger.w(LogTarget.rom(result.rom), "Imported file is a rom already included in romset, skipping import");
            }
            catch (Exception e)
            {
              // TODO: handle and write on log

              e.printStackTrace();
            }  
          }
          else
            logger.w(LogTarget.file(file), "The file is not any recognized rom for this romset");
        }
      }
    }
    }.start();
  }
}
