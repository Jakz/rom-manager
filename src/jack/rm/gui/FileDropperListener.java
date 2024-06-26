package jack.rm.gui;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.TransferHandler;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.files.Organizer;
import jack.rm.files.ScanResult;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public class FileDropperListener implements FileTransferHandler.Listener
{
  private static final Logger logger = Log.getLogger(LogSource.IMPORTER);
  
  @Override
  public void filesDropped(TransferHandler.TransferSupport info, Path[] files)
  {
    new Thread()
    {
      
    @Override
    public void run()
    {
      final MyGameSetFeatures helper = Main.current.helper();
      
      Path romsPath = helper.settings().romsPath;
      
      for (Path file : files)
      {
        if (Files.isRegularFile(file))
        {
          System.out.println("Processing "+file.getFileName());
     
          List<ScanResult> results = helper.scanner().singleBlockingCheck(file);
        
          if (results != null)
          {
            results.forEach(result -> {          
              try
              {            
                // a missing rom has been dropped on list
                if (!result.rom.isPresent())
                {
                  result.assign();
                  
                  Rom rom = result.rom;
                  Game game = rom.game();
                  
                  // first let's copy the file in the rompath
                  Path romFile = rom.handle().path();
                  if (!romFile.getParent().equals(romsPath))
                  {
                    Path destFile = romsPath.resolve(romFile.getFileName());
                    //game.move(destFile);       //TODO: relocate          
                  }
                  
                  game.updateStatus();
                  
                  helper.organizer().organizeRomIfNeeded(rom);
                  
                  Main.current.refreshStatus();
                  Main.mainFrame.rebuildGameList();
                  
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
            
            });
          }
          else
            logger.w(LogTarget.file(file), "The file is not any recognized rom for this romset");
        }
      }
    }
    }.start();
  }
}
