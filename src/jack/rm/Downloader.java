package jack.rm;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.gui.ProgressDialog;
import jack.rm.gui.Callback;

import java.util.concurrent.*;
import java.nio.channels.*;
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
				
		ProgressDialog.init(Main.mainFrame, "Art Download", new Callback() { public void call() { pool.shutdownNow(); started = false; } });
	}
	
	public static class TwinArtDownloaderTask implements Callable<Boolean>
	{
		String urlt, patht, urlg, pathg;
		Rom rom;
		
		public TwinArtDownloaderTask(Rom rom)
		{
			patht = RomSet.current.titleImage(rom);
			urlt = RomSet.current.titleImageURL(rom);

			pathg = RomSet.current.gameImage(rom);
			urlg = RomSet.current.gameImageURL(rom);

			this.rom = rom;
		}
		
		public Boolean call()
		{
			try
			{
				URL realUrl = new URL(urlt);
				ReadableByteChannel rbc = Channels.newChannel(realUrl.openStream());
				FileOutputStream fos = new FileOutputStream(patht);
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				fos.close();
				
				realUrl = new URL(urlg);
				rbc = Channels.newChannel(realUrl.openStream());
				fos = new FileOutputStream(pathg);
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				fos.close();
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
		String url, path;
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
		
		public Boolean call()
		{
		  FileOutputStream fos = null;
		  try
			{
				URL realUrl = new URL(url);
				ReadableByteChannel rbc = Channels.newChannel(realUrl.openStream());
				fos = new FileOutputStream(path);
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
				fos.close();
			}
			catch (FileNotFoundException e)
			{
				//Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+".");
				Main.infoPanel.updateFields(rom);
				return false;
			}
			catch (Exception e)
			{
				try
				{
				  if (fos != null)
				    fos.close();
				}
				catch (IOException ee)
				{
				  ee.printStackTrace();
				}
			  
			  File partialDownload = new File(path);
				if (partialDownload.exists())
				{
				  //Main.logln("Deleting partial artwork for "+Renamer.formatNumber(rom.number));
				  partialDownload.delete();
				}

				return false;
			}
			
			//Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+" ("+type+").");
			
			long completed = pool.getCompletedTaskCount();
			long total = pool.getTaskCount(); 
			
			ProgressDialog.update(completed/(float)total, completed+" of "+total);
			
	    return true;
		}
	}
}
