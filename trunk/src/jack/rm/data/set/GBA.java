package jack.rm.data.set;

import jack.rm.Paths;
import jack.rm.data.Renamer;
import jack.rm.data.Rom;

import java.awt.Dimension;

class GBA extends RomSet
{
	public GBA()
	{
		super(Console.GBA, Provider.OFFLINELIST, "roms/gba/", new Dimension(480,320), new Dimension(480,320));
	}
	
	public String titleImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return "http://offlinelistgba.free.fr/imgs/"+partial+(rom.imageNumber)+"a.png";
	}
	
	public String gameImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return "http://offlinelistgba.free.fr/imgs/"+partial+(rom.imageNumber)+"b.png";
	}
	
	public String titleImage(Rom rom)
	{
		return Paths.screensTitle()+Renamer.formatNumber(rom.number)+".png";
	}
	
	public String gameImage(Rom rom)
	{
		return Paths.screensGame()+Renamer.formatNumber(rom.number)+".png";
	}
	
	public String downloadURL(Rom rom)
	{
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=31";
		
		String name = rom.title.replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}