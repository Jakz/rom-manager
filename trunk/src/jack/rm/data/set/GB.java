package jack.rm.data.set;

import jack.rm.Paths;
import jack.rm.data.Renamer;
import jack.rm.data.Rom;

import java.awt.Dimension;

class GB extends RomSetOfflineList
{
	public GB()
	{
		super(Console.GB, Provider.NOINTRO, new Dimension(320,288), new Dimension(320,288),"http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/");
	}

	public String downloadURL(Rom rom)
	{
		//TODO FIX
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=11";
		
		String name = rom.title.replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}