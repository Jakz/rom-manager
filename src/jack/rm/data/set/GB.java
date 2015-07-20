package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

class GB extends RomSetOfflineList
{
	public GB() throws MalformedURLException
	{
		super(System.GB, ProviderID.NOINTRO, new Dimension(320,288), new Dimension(320,288), new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/"));
	}

	@Override
  public String downloadURL(Rom rom)
	{
		//TODO FIX
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=11";
		
		String name = rom.getTitle().replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}