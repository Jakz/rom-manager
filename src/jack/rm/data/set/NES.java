package jack.rm.data.set;

import jack.rm.data.Rom;

import java.awt.Dimension;

class NES extends RomSetOfflineList
{
	public NES()
	{
		super(Console.NES, Provider.NOINTRO, new Dimension(256,240), new Dimension(256,240),"http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20NES%20-%20Famicom/");
	}

	public String downloadURL(Rom rom)
	{
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=13";
		
		String name = rom.title.replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}