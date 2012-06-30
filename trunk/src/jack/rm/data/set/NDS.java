package jack.rm.data.set;

import jack.rm.Paths;
import jack.rm.data.Renamer;
import jack.rm.data.Rom;

import java.awt.Dimension;

class NDS extends RomSet
{
	NDS()
	{
		super(Console.NDS, Provider.ADVANSCENE, "/Volumes/Vicky/Roms/roms/nds", new Dimension(214,384), new Dimension(256,384));
	}
	
	public String titleImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return "http://www.retrocovers.com/offline/imgs/ADVANsCEne_NDS/"+partial+(rom.imageNumber)+"a.png";
	}
	
	public String gameImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return "http://www.retrocovers.com/offline/imgs/ADVANsCEne_NDS/"+partial+(rom.imageNumber)+"b.png";
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
	
	public String titleImage(Rom rom)
	{
		return Paths.screensTitle()+Renamer.formatNumber(rom.imageNumber)+".png";
	}
	
	public String gameImage(Rom rom)
	{
		return Paths.screensGame()+Renamer.formatNumber(rom.imageNumber)+".png";
	}
	
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