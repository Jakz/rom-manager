package jack.rm.data.set;

import jack.rm.data.Rom;

import java.awt.Dimension;

class WS extends RomSetOfflineList
{
	public WS()
	{
		super(Console.WS, Provider.NOINTRO, new Dimension(448,448), new Dimension(448,448),"http://nointro.free.fr/imgs/Official%20No-Intro%20Bandai%20WonderSwan/");
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