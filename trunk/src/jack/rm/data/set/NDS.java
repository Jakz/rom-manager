package jack.rm.data.set;

import jack.rm.data.Rom;

import java.awt.Dimension;

class NDS extends RomSetOfflineList
{
	NDS()
	{
		super(Console.NDS, Provider.ADVANSCENE, new Dimension(214,384), new Dimension(256,384),"http://www.retrocovers.com/offline/imgs/ADVANsCEne_NDS/");
	}

	/*
	public String titleImageURL(Rom rom)
	{
		return "http://advanscene.com/html/Releases/dsboxart/"+Renamer.formatNumber(rom.imageNumber)+"-3.jpg";
	}*/
	/*
	public String gameImageURL(Rom rom)
	{
		return "http://advanscene.com/html/Releases/imr2.php?id="+rom.imageNumber;
	}*/

	public String downloadURL(Rom rom)
	{
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=32";
		
		String name = rom.title.replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}