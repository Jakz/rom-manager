package jack.rm.assets;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;

import java.util.concurrent.*;

import com.pixbits.gui.ProgressDialog;

import java.nio.channels.*;
import java.nio.file.*;
import java.net.*;
import java.io.*;

public class Downloader
{
  public ThreadPoolExecutor pool;
  int totalTasks;
  int missingTasks;
  boolean started;
  
  private final RomSet set;

  public Downloader(RomSet set)
  {
    this.set = set;
  }
  
  public void start()
  {
    if (started)
      return;
    
    pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
    started = true;
    
    Asset[] assets = set.getAssetManager().getSupportedAssets();
    
    for (int i = 0; i < set.list.count(); ++i)
    {
      Rom r = set.list.get(i);
      
      for (Asset asset : assets)
        if (!r.hasAsset(asset))
          pool.submit(new ArtDownloaderTask(r, asset));
    }
    
    pool.shutdown();
        
    ProgressDialog.init(Main.mainFrame, "Asset Download", () -> { pool.shutdownNow(); started = false; });
  }
  
  public void downloadArt(final Rom r)
  {    
    new Thread()
    {
      @Override
      public void run()
      {        
        Asset[] assets = set.getAssetManager().getSupportedAssets();

        for (Asset asset : assets)
          if (!r.hasAsset(asset))
            new ArtDownloaderTask(r, asset).call();
        
        // TODO: doesn't work if user changed the rom while it was downloading
        Main.mainFrame.updateInfoPanel(r);
      }
    }.start();
  }
  
  public class ArtDownloaderTask implements Callable<Boolean>
  {
    URL url;
    Path path;
    Asset asset;
    Rom rom;
    
    public ArtDownloaderTask(Rom rom, Asset asset)
    {  
      path = rom.getAssetData(asset).getFinalPath();
      url = set.getAssetManager().assetURL(asset, rom);
                
      this.rom = rom;
      this.asset = asset;
    }
    
    @Override
    public Boolean call()
    {
      try
      {
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        channel.transferFrom(rbc, 0, 1 << 24);
        channel.close();
      }
      catch (FileNotFoundException e)
      {
        Log.log(LogType.ERROR, LogSource.DOWNLOADER, LogTarget.rom(rom), "Asset not found at "+url);
        Main.mainFrame.updateInfoPanel(rom);
        return false;
      }
      catch (java.nio.channels.ClosedByInterruptException e)
      {
        try
        {
          if (Files.exists(path))
            Files.delete(path);
        }
        catch (IOException ee)
        {
          ee.printStackTrace();
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();

        return false;
      }

      if (pool != null)
      {
        long completed = pool.getCompletedTaskCount();
        long total = pool.getTaskCount(); 
      
        ProgressDialog.update(completed/(float)total, completed+" of "+total);
      }

      return true;
    }
  }
}
