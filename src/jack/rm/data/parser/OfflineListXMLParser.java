package jack.rm.data.parser;

import jack.rm.data.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.CharArrayWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jack.rm.data.console.GBA;

public class OfflineListXMLParser extends DefaultHandler
{
	CharArrayWriter buffer = new CharArrayWriter();
	
	static final private Map<Integer, Language> languageMap = new HashMap<>();
	
	static
	{
	  languageMap.put(1, Language.FRENCH);
	  languageMap.put(2, Language.ENGLISH);
	  languageMap.put(4, Language.CHINESE);
	  languageMap.put(8, Language.DANISH);
	  languageMap.put(16, Language.DUTCH);
	  languageMap.put(32, Language.FINNISH);
	  languageMap.put(64, Language.GERMAN);
	  languageMap.put(128, Language.ITALIAN);
	  languageMap.put(256, Language.JAPANESE);
	  languageMap.put(512, Language.NORWEGIAN);
	  languageMap.put(1024, Language.POLISH);
	  languageMap.put(2048, Language.PORTUGUESE);
	  languageMap.put(4096, Language.SPANISH);
	  languageMap.put(8192, Language.SWEDISH);
	  languageMap.put(16384, Language.ENGLISH_UK);
	  languageMap.put(32768, Language.PORTUGUESE_BR);
	  languageMap.put(65536, Language.KOREAN);
	}
	
	NumberedRom rom;
	int curString;
	
	boolean started = false;
	
	RomList romList;
	
	
	public OfflineListXMLParser(RomList romList)
	{
		this.romList = romList;
	}

	@Override
  public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException
	{			
		if (localName.equals("game"))
		{
			rom = new NumberedRom();
		}
		else if (localName.equals("games"))
		{
			started = true;
		}
		
		buffer.reset();
	}
	
	public String asString()
	{
		return buffer.toString().replaceAll("[\r\n]"," ").trim();
	}
	
	public int asInt()
	{
		return Integer.parseInt(asString());
	}
	
	public long asLong()
	{
	  return Long.parseLong(asString());
	}
	
	public GBA.Save parseSave(String string)
	{
	  String[] tokens = string.split("_");
	  
	  if (tokens.length == 1 && tokens[0].compareToIgnoreCase(GBA.Save.Type.NONE.toString()) == 0)
	    return new GBA.Save(GBA.Save.Type.NONE);
	  else if (tokens.length >= 2)
	  {
	    if (tokens.length > 2)
	      tokens[1] = tokens[2];
	    
	    for (GBA.Save.Type type : GBA.Save.Type.values())
	    {
	      if (tokens[0].toLowerCase().contains(type.toString().toLowerCase()))
	        return new GBA.Save(type, Integer.valueOf(tokens[1].substring(1)));
	    }
	  }
	  
	  return null;
	}
	
  Set<String> saves = new HashSet<>();
	
	@Override
  public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		if (!started)
			return;
		
		switch(localName)
		{
		  case "imageNumber": rom.imageNumber = asInt(); break;
		  case "releaseNumber": rom.number = asInt(); break;
		  case "title": rom.title = asString(); break;
		  case "saveType":
		  {
		    RomSave<?> save = parseSave(asString());
		    rom.setSave(save);
		    break;
		  }
		  case "romSize": rom.size = RomSize.forBytes(asLong()); break;
		  case "publisher": rom.publisher = asString(); break;
		  case "location": rom.location = Location.get(asInt()); break;
		  case "language":
		  {
		    int values = asInt();
		    languageMap.forEach( (k, v) -> { if ((values & k) != 0) rom.languages.add(v); });
		    break;
		  }
		  case "sourceRom": rom.group = asString(); break;
		  case "romCRC": rom.crc = Long.parseLong(asString(), 16); break;
      case "im1CRC": rom.imgCRC1 = Long.parseLong(asString(), 16); break;
      case "im2CRC": rom.imgCRC2 = Long.parseLong(asString(), 16); break;
      case "comment": rom.info = asString(); break;
      case "game": romList.add(rom); break;
      case "games":
      {
        romList.sort(); 
        saves.forEach(s -> System.out.println(s));
        break;
      }

		}
	}
	
	@Override
  public void characters(char[] ch, int start, int length)
	{
		buffer.write(ch,start,length);
	}
}