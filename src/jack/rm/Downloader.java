package jack.rm;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.gui.ProgressDialog;
import jack.rm.gui.Callback;

import java.util.concurrent.*;
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

	Downloader()
	{
	}
	
	public void start()
	{
		if (started)
			return;
		
    pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
		started = true;
		
		for (int i = 0; i < Main.romList.count(); ++i)
		{
			Rom r = Main.romList.get(i);
			
			if (!r.hasTitleArt())
				pool.submit(new ArtDownloaderTask(r, "title"));
			if (!r.hasGameArt())
				pool.submit(new ArtDownloaderTask(r, "game"));
			
		}
				
		ProgressDialog.init(Main.mainFrame, "Art Download", new Callback() { @Override
    public void call() { pool.shutdownNow(); started = false; } });
	}
	
	public void downloadArt(final Rom r)
	{
	  new Thread()
	  {
	    @Override
      public void run()
	    {
	      if (!r.hasTitleArt())
	        new ArtDownloaderTask(r, "title").call();
	      if (!r.hasGameArt())
	        new ArtDownloaderTask(r, "game").call();
	      
	      Main.infoPanel.updateFields(r);
	    }
	  }.run();
	}
	
	public static class TwinArtDownloaderTask implements Callable<Boolean>
	{
		URL urlt, urlg;
		Path patht, pathg;
		Rom rom;
		
		public TwinArtDownloaderTask(Rom rom)
		{
			patht = RomSet.current.titleImage(rom);
			urlt = RomSet.current.titleImageURL(rom);

			pathg = RomSet.current.gameImage(rom);
			urlg = RomSet.current.gameImageURL(rom);

			this.rom = rom;
		}
		
		@Override
    public Boolean call()
		{
			try
			{
				ReadableByteChannel rbc = Channels.newChannel(urlt.openStream());
				FileChannel channel = FileChannel.open(patht, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
				channel.transferFrom(rbc, 0, 1 << 24);
				channel.close();
				
				rbc = Channels.newChannel(urlg.openStream());
				channel = FileChannel.open(pathg);
				channel.transferFrom(rbc, 0, 1 << 24);
				channel.close();
			}
			catch (FileNotFoundException e)
			{
				//Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+".");
				Main.infoPanel.updateFields(rom);
				return false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}

			//Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+".");
			Main.infoPanel.updateFields(rom);
			return true;
		}
	}
	
	public class ArtDownloaderTask implements Callable<Boolean>
	{
		URL url;
		Path path;
		String type;
		Rom rom;
		
		public ArtDownloaderTask(Rom rom, String type)
		{	
			if (type.equals("title"))
			{
				path = RomSet.current.titleImage(rom);
				url = RomSet.current.titleImageURL(rom);
			}
			else
			{
				path = RomSet.current.gameImage(rom);
				url = RomSet.current.gameImageURL(rom);
			}
					
			this.rom = rom;
			this.type = type;
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
				//Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+".");
				Main.infoPanel.updateFields(rom);
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
			
			//Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+" ("+type+").");
			
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
