package jack.rm.data.parser;

import jack.rm.data.RomSet;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class DatLoader
{
	public static void load()
	{
		loadDat(new AdvanceSceneXMLParser());
	}
	
	public static void loadDat(DefaultHandler handler)
	{
		try
		{
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			
			reader.parse(RomSet.current.datPath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
