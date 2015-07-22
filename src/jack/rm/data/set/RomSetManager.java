package jack.rm.data.set;

import java.util.*;

import jack.rm.data.*;
import jack.rm.data.console.System;
import jack.rm.log.*;

public class RomSetManager
{
	private static Map<System, RomSet<? extends Rom>> sets = new HashMap<>();
	
	static
	{
		try
		{
		  sets.put(System.GBA, new GBA());
		  sets.put(System.NDS, new NDS());
		  sets.put(System.GBC, new GBC());
		  sets.put(System.NES, new NES());
		  sets.put(System.GB, new GB());
		  sets.put(System.WS, new WS());
		  sets.put(System._3DS, new _3DS());
		}
		catch (Exception e)
		{
		  e.printStackTrace();
		}
	}
	
	public static RomSet<?> bySystem(System system)
	{
	   return sets.values().stream().filter( rs -> rs.system == system).findFirst().orElse(null);
	}
	
	public static RomSet<?> byIdent(String ident)
	{
		return sets.values().stream().filter( rs -> rs.ident().equals(ident)).findFirst().orElse(null);
	}
	
	public static Collection<RomSet<? extends Rom>> sets()
	{
		return sets.values();
	}
	
	public static RomSet<? extends Rom> loadSet(System console)
	{
		return loadSet(sets.get(console));
	}
	
	public static RomSet<? extends Rom> loadSet(RomSet<? extends Rom> set)
	{
		Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(set), "Loading romset");
	  			
		RomSize.mapping.clear();
		set.load();
						
		return set;
	}
}
