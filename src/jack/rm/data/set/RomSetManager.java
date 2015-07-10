package jack.rm.data.set;

import java.io.File;
import java.util.*;

import jack.rm.*;
import jack.rm.data.RomSize;
import jack.rm.log.*;

public class RomSetManager
{
	private static Map<Console, RomSet> sets = new HashMap<Console, RomSet>();
	
	static
	{
		sets.put(Console.GBA, new GBA());
		sets.put(Console.NDS, new NDS());
		sets.put(Console.GBC, new GBC());
		sets.put(Console.NES, new NES());
		sets.put(Console.GB, new GB());
		sets.put(Console.WS, new WS());
		
		Settings.load();
	}
	
	public static RomSet byIdent(String ident)
	{
		for (RomSet rs : sets.values())
		{
			if (rs.ident().equals(ident))
				return rs;
		}
		
		return null;
	}
	
	public static Collection<RomSet> sets()
	{
		return sets.values();
	}
	
	public static void loadSet(Console console)
	{
		loadSet(sets.get(console));
	}
	
	public static void loadSet(RomSet set)
	{
		Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(set), "Loading romset");
	  		
		RomSet.current = set;
		
		new File(Paths.screensTitle()).mkdirs();
		new File(Paths.screensGame()).mkdirs();
		//new File(set.romPath).mkdirs();
		
		Main.romList.clear();
		
		RomSize.mapping.clear();
		set.load();
			
		Main.searchPanel.resetFields(RomSize.mapping.values().toArray(new RomSize[RomSize.mapping.size()]));
		Main.mainFrame.romListModel.fireChanges();
		Main.mainFrame.updateCbRomSet(set);
		Main.infoPanel.setScreenSizes(set.screenTitle,set.screenGame);
		
		Main.scanner.scanForRoms(!PersistenceRom.load(set));
		
		Main.romList.showAll();
	}
}
