package jack.rm.gui;

import java.nio.file.Files;
import java.nio.file.Path;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.Main;
import jack.rm.data.romset.GameSet;
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
      Path romsPath = GameSet.current.getSettings().romsPath;
      
      for (Path file : files)
      {
        
        if (Files.isRegularFile(file))
        {
          System.out.println("Processing "+file.getFileName());
  
          
          ScanResult result = GameSet.current.getScanner().scanFile(file);
        
          if (result != null)
          {
            try
            {
            
            // a missing rom has been dropped on list
            if (result.rom.status == GameStatus.MISSING)
            {
              result.assign();
              Game rom = result.rom;
              
              // first let's copy the file in the rompath
              Path romFile = rom.getHandle().path();
              if (!romFile.getParent().equals(romsPath))
              {
                Path destFile = romsPath.resolve(romFile.getFileName());
                rom.move(destFile);
                
              }
              
              rom.status = GameStatus.FOUND;
              
              Organizer.organizeRomIfNeeded(rom);
              
              GameSet.current.list.updateStatus();
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
