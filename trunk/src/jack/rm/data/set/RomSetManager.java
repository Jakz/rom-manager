package jack.rm.data.set;

import java.util.*;

import jack.rm.Main;
import jack.rm.data.RomSize;
import jack.rm.data.set.RomSet.GB;
import jack.rm.data.set.RomSet.GBA;
import jack.rm.data.set.RomSet.GBC;
import jack.rm.data.set.RomSet.NDS;
import jack.rm.data.set.RomSet.NES;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

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
	}
	
	public static Collection<RomSet> sets()
	{
		return sets.values();
	}
	
	private static void loadDat(DefaultHandler handler, String path)
	{
		try
		{
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			
			reader.parse(path);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void loadSet(Console console)
	{
		loadSet(sets.get(console));
	}
	
	public static void loadSet(RomSet set)
	{
		Main.logln("Loading romset: "+set+"..");
		
		RomSet.current = set;
		Main.romList.clear();
		
		RomSize.mapping.clear();
		loadDat(set.buildDatLoader(Main.romList), set.datPath);
		
		Main.searchPanel.resetFields(RomSize.mapping.values().toArray(new RomSize[RomSize.mapping.size()]));
		Main.mainFrame.romListModel.fireChanges();
		Main.mainFrame.updateCbRomSet(set);
		Main.infoPanel.setScreenSizes(set.screenTitle,set.screenGame);
		
		Main.scanner.scanForRoms();
		Main.romList.showAll();
	}
}
