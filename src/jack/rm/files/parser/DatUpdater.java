package jack.rm.files.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.Main;
import jack.rm.data.romset.RomSet;
import jack.rm.files.BackgroundOperation;
import jack.rm.files.DownloadWorker;
import jack.rm.files.ZipExtractWorker;
import jack.rm.gui.Dialogs;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public class DatUpdater
{
  private static final Logger logger = Log.getLogger(LogSource.DAT_DOWNLOADER);
  
  public static void updateDat(RomSet set, Consumer<Boolean> callback) throws IOException
  {
    Path destDat = set.datPath();
    AtomicReference<Path> tmpDownloadPath = new AtomicReference<Path>(Files.createTempFile(null, null));
    AtomicReference<Path> tmpZippedPath = new AtomicReference<Path>();

    AtomicLong crc = new AtomicLong(-1);
    AtomicLong size = new AtomicLong(-1);
    
    /* if dat file exists we first compute the CRC and size of the current file to compare with the one that is going to be downloaded */
    if (set.canBeLoaded())
    {
      crc.set(FileUtils.calculateCRCFast(destDat));
      size.set(Files.size(destDat));
    }
    
    final Consumer<Boolean> consolidationStep = r -> {
      try
      {
      
        Path src = tmpDownloadPath.get();
      
        if (crc.get() != -1)
        {
          long ncrc = FileUtils.calculateCRCFast(src);
          long nsize = Files.size(src);
          
          if (ncrc == crc.get() && nsize == size.get())
          {
            Dialogs.showWarning("Dat alrady up-to-date", "Your DAT version is already up to date!", Main.gsettingsView);
            return;
          }
        }
        
        Files.move(src, destDat, StandardCopyOption.REPLACE_EXISTING);

      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    };
    
    final Consumer<Boolean> cleanupStep = r -> {
      try
      {
        if (tmpDownloadPath.get() != null) Files.deleteIfExists(tmpDownloadPath.get());
        if (tmpZippedPath.get() != null) Files.deleteIfExists(tmpZippedPath.get());

      }
      catch (IOException e)
      {
        e.printStackTrace();
        callback.accept(false);
      }
    };
    
    Consumer<Boolean> extractionStep = r -> { 
      boolean isZipped = false;
      
      try
      {
        ZipFile zfile = new ZipFile(tmpDownloadPath.get().toFile());
        isZipped = true;
        zfile.close();
      }
      catch (IOException e) { }

      try
      {
        if (isZipped)
        {
          tmpZippedPath.set(tmpDownloadPath.get());
          tmpDownloadPath.set(Files.createTempFile(null, null));
          logger.i(LogTarget.romset(set), "Extracting DAT for "+set.ident()+" to "+tmpDownloadPath);

          
          ZipExtractWorker<?> worker =  new ZipExtractWorker<BackgroundOperation>(tmpZippedPath.get(), tmpDownloadPath.get(), new BackgroundOperation() {
            public String getTitle() { return "Uncompressing"; }
            public String getProgressText() { return "Progress.."; }
            }, consolidationStep.andThen(cleanupStep).andThen(callback), Main.gsettingsView);
            
          worker.execute();
        }
        else
          consolidationStep.andThen(cleanupStep).andThen(callback).accept(true);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        callback.accept(false);
      } 
    };
    
    logger.i(LogTarget.romset(set), "Downloading DAT for "+set.ident()+" to "+tmpDownloadPath);
    
    DownloadWorker<?> worker = new DownloadWorker<BackgroundOperation>(set.provider.getSource().getURL(), tmpDownloadPath.get(), new BackgroundOperation() {
      public String getTitle() { return "Downloading"; }
      public String getProgressText() { return "Progress.."; }
    }, extractionStep, Main.gsettingsView, set.provider.getSource().getPostArguments());
    
    worker.execute();    
    
  }
}
