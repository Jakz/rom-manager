package jack.rm.data.set;

import jack.rm.Paths;
import jack.rm.data.Renamer;
import jack.rm.data.Rom;

import java.awt.Dimension;

class NES extends RomSet
{
	public NES()
	{
		super(Console.NES, Provider.NOINTRO, "roms/nes/", new Dimension(256,240), new Dimension(256,240));
	}
	
	public String titleImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return "http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20NES%20-%20Famicom/"+partial+(rom.imageNumber)+"a.png";
	}
	
	public String gameImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return "http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20NES%20-%20Famicom/"+partial+(rom.imageNumber)+"b.png";
	}
	
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
		String query2 = "&section=roms&sysid=11";
		
		String name = rom.title.replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}