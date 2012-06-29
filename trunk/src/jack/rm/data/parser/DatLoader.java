package jack.rm.data.parser;


import jack.rm.Main;
import jack.rm.data.RomSize;
import jack.rm.data.set.RomSet;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class DatLoader
{
	public static void load()
	{
		loadDat(new OfflineListXMLParser(Main.romList));
	}
	
	public static void loadDat(DefaultHandler handler)
	{
		try
		{
			Main.romList.clear();
			
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			
			reader.parse(RomSet.current.datPath);
			
			Main.searchPanel.updateSizes(RomSize.mapping.values().toArray(new RomSize[RomSize.mapping.size()]));
			Main.mainFrame.romListModel.fireChanges();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
