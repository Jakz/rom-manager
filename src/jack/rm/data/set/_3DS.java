package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

class _3DS extends RomSetOfflineList
{
	_3DS() throws MalformedURLException
	{
		super(System._3DS, ProviderID.ADVANSCENE, new Dimension(268,240), new Dimension(268,240), new URL("http://www.advanscene.com/offline/imgs/ADVANsCEne_3DS/"));
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

	@Override
  public String downloadURL(Rom rom)
	{
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=32";
		
		String name = rom.getTitle().replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}