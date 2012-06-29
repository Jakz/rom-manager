package jack.rm;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;

import java.util.concurrent.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.io.*;

public class Downloader
{
	ExecutorService pool;
	boolean started;
	
	Downloader()
	{
		pool = Executors.newFixedThreadPool(10);
	}
	
	public void start()
	{
		if (started)
			return;
		
		started = true;
		
		for (int i = 0; i < Main.romList.count(); ++i)
		{
			Rom r = Main.romList.get(i);
			
			if (!r.hasTitleArt())
				pool.submit(new ArtDownloaderTask(r, "title"));
			if (!r.hasGameArt())
				pool.submit(new ArtDownloaderTask(r, "game"));
		}
	}
	
	class ArtDownloaderTask implements Callable<Boolean>
	{
		String url, path;
		String type;
		Rom rom;
		
		/*ArtDownloaderTask(Rom rom, String type)
		{
			if (type.equals("title"))
			{
				path = Paths.screensTitle+Renamer.formatNumber(rom.number)+".png";
				url = Paths.screensTitleURL+Renamer.formatNumber(rom.imageNumber)+"-1.png";
			}
			else
			{
				path = Paths.screensGame+Renamer.formatNumber(rom.number)+".png";
				url = Paths.screensGameURL+Renamer.formatNumber(rom.imageNumber)+"-2.png";
			}
					
			this.rom = rom;
			this.type = type;
		}*/
		
		ArtDownloaderTask(Rom rom, String type)
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
			try
			{
				URL realUrl = new URL(url);
				ReadableByteChannel rbc = Channels.newChannel(realUrl.openStream());
				FileOutputStream fos = new FileOutputStream(path);
				fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
			
			Main.logln("Downloaded art for "+Renamer.formatNumber(rom.number)+" ("+type+").");
	    return true;
		}
	}
}
