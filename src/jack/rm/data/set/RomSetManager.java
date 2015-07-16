package jack.rm.data.set;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import jack.rm.*;
import jack.rm.data.*;
import jack.rm.log.*;

public class RomSetManager
{
	private static Map<Console, RomSet<? extends Rom>> sets = new HashMap<>();
	
	static
	{
		try
		{
		  sets.put(Console.GBA, new GBA());
		  sets.put(Console.NDS, new NDS());
		  sets.put(Console.GBC, new GBC());
		  sets.put(Console.NES, new NES());
		  sets.put(Console.GB, new GB());
		  sets.put(Console.WS, new WS());
		  sets.put(Console._3DS, new _3DS());
		}
		catch (Exception e)
		{
		  e.printStackTrace();
		}
		
		Settings.load();
	}
	
	public static RomSet<?> byIdent(String ident)
	{
		for (RomSet<?> rs : sets.values())
		{
			if (rs.ident().equals(ident))
				return rs;
		}
		
		return null;
	}
	
	public static Collection<RomSet<? extends Rom>> sets()
	{
		return sets.values();
	}
	
	public static RomSet<? extends Rom> loadSet(Console console)
	{
		return loadSet(sets.get(console));
	}
	
	public static RomSet<? extends Rom> loadSet(RomSet<? extends Rom> set)
	{
		Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(set), "Loading romset");
	  		
		RomSet.current = set;
		
		try
		{
		  for (Asset asset : set.getSupportedAssets())
		    Files.createDirectories(Settings.getAssetPath(asset));
		}
		catch (IOException e)
		{
		  e.printStackTrace();
		  // TODO: log
		}

		RomSize.mapping.clear();
		set.load();
		
		Main.mainFrame.romSetLoaded(set);
				
		return set;
	}
}
