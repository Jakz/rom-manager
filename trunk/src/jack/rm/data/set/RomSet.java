package jack.rm.data.set;

import java.net.URI;
import java.util.*;

import jack.rm.Paths;
import jack.rm.data.*;

import java.awt.Desktop;
import java.awt.Dimension;

public abstract class RomSet
{
	public static final List<RomSet> sets = new ArrayList<RomSet>();
	public static RomSet current;
	
	static
	{
		sets.add(new GBA());
		sets.add(new NDS());
		sets.add(new GBC());
		sets.add(new NES());
		sets.add(new GB());
		
		current = sets.get(3);
	}

	public final Console type;
	public final String romPath;
	public final String name;
	public final String datPath;
	
	public final Dimension screenTitle;
	public final Dimension screenGame;
	
	RomSet(Console type, String name, String romPath, String datPath, Dimension screenTitle, Dimension screenGame)
	{
		this.type = type;
		this.romPath = romPath;
		this.name = name;
		this.datPath = datPath;
		this.screenTitle = screenTitle;
		this.screenGame = screenGame;
	}
	
	public abstract String titleImageURL(Rom rom);
	public abstract String gameImageURL(Rom rom);
	public abstract String titleImage(Rom rom);
	public abstract String gameImage(Rom rom);
	public abstract String downloadURL(Rom rom);
	
	static class GBA extends RomSet
	{
		public GBA()
		{
			super(Console.GBA, "gba", "roms/gba/", "dat/ol-gba.xml", new Dimension(480,320), new Dimension(480,320));
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
	
	static class GBC extends RomSet
	{
		public GBC()
		{
			super(Console.GBC, "gbc", "roms/gbc/", "dat/ni-gbc.xml", new Dimension(320,288), new Dimension(320,288));
		}
		
		public String titleImageURL(Rom rom)
		{
			String partial = ((((rom.imageNumber-1)/500)*500)+1)+"-"+((((rom.imageNumber-1)/500+1)*500))+"/";
			return "http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy%20Color/"+partial+(rom.imageNumber)+"a.png";
		}
		
		public String gameImageURL(Rom rom)
		{
			String partial = ((((rom.imageNumber-1)/500)*500)+1)+"-"+((((rom.imageNumber-1)/500+1)*500))+"/";
			return "http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy%20Color/"+partial+(rom.imageNumber)+"b.png";
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
			//TODO FIX
			
			String query1 = "http://www.emuparadise.me/roms/search.php?query=";
			String query2 = "&section=roms&sysid=11";
			
			String name = rom.title.replaceAll("\\W", " ").toLowerCase();
			name = name.replace(" ","%20");
			// Renamer.formatNumber(rom.imageNumber)
		
			return query1+name+query2;
		}
	}
	
	static class GB extends RomSet
	{
		public GB()
		{
			super(Console.GBC, "gb", "roms/gb/", "dat/ni-gb.xml", new Dimension(320,288), new Dimension(320,288));
		}
		
		public String titleImageURL(Rom rom)
		{
			String partial = ((((rom.imageNumber-1)/500)*500)+1)+"-"+((((rom.imageNumber-1)/500+1)*500))+"/";
			return "http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/"+partial+(rom.imageNumber)+"a.png";
		}
		
		public String gameImageURL(Rom rom)
		{
			String partial = ((((rom.imageNumber-1)/500)*500)+1)+"-"+((((rom.imageNumber-1)/500+1)*500))+"/";
			return "http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/"+partial+(rom.imageNumber)+"b.png";
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
			//TODO FIX
			String query1 = "http://www.emuparadise.me/roms/search.php?query=";
			String query2 = "&section=roms&sysid=11";
			
			String name = rom.title.replaceAll("\\W", " ").toLowerCase();
			name = name.replace(" ","%20");
			// Renamer.formatNumber(rom.imageNumber)
		
			return query1+name+query2;
		}
	}
	
	static class NDS extends RomSet
	{
		NDS()
		{
			super(Console.NDS, "nds", "roms/nds/", "dat/as-nds.xml", new Dimension(384,384), new Dimension(256,384));
		}
		
		public String titleImageURL(Rom rom)
		{
			return "http://advanscene.com/html/Releases/dsboxart/"+Renamer.formatNumber(rom.imageNumber)+"-3.jpg";
		}
		
		public String gameImageURL(Rom rom)
		{
			return "http://advanscene.com/html/Releases/imr2.php?id="+rom.imageNumber;
		}
		
		public String titleImage(Rom rom)
		{
			return Paths.screensTitle()+Renamer.formatNumber(rom.imageNumber)+".jpg";
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
	
	static class NES extends RomSet
	{
		public NES()
		{
			super(Console.NES, "nes", "roms/nes/", "dat/ni-nes.xml", new Dimension(256,240), new Dimension(256,240));
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

}
