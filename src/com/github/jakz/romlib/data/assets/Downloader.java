package com.github.jakz.romlib.data.assets;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import jack.rm.Main;
import jack.rm.gui.Dialogs;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public class Downloader
{
  private static final Logger logger = Log.getLogger(LogSource.DOWNLOADER);
  
  public ThreadPoolExecutor pool;
  int totalTasks;
  int missingTasks;
  boolean started;
  
  private final GameSet set;

  public Downloader(GameSet set)
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
    
    set.stream().forEach(g -> {
      for (Asset asset : assets)
        if (!g.hasAsset(asset))
          pool.submit(new ArtDownloaderTask(g, asset));
    });
    
    pool.shutdown();
        
    if (!pool.getQueue().isEmpty())
    {
      Main.progress.show(Main.mainFrame, "Asset Download", () -> { pool.shutdownNow(); started = false; });
      
      new Thread( () -> {
        try
        {
          pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
          if (!pool.isShutdown())
            Main.progress.finished();
        }
        catch (InterruptedException e)
        {
          // cancelled by user
        }
        
      }).start();
    }
    else
      Dialogs.showMessage("Asset Downloader", "All the assets have already been downloaded.", Main.mainFrame);
  }
  
  public void downloadArt(final Game r)
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
    Game rom;
    
    public ArtDownloaderTask(Game rom, Asset asset)
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
        logger.e(LogTarget.game(rom), "Asset not found at "+url);
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
      
        Main.progress.update(completed/(float)total, (completed+1)+" of "+total);
      }

      return true;
    }
  }
}