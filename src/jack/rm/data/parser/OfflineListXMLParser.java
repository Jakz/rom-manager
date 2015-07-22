package jack.rm.data.parser;

import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.CharArrayWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jack.rm.data.console.GBA;
import jack.rm.data.rom.RomAttribute;

public class OfflineListXMLParser extends DefaultHandler
{
	CharArrayWriter buffer = new CharArrayWriter();
	
	private final DecimalFormat format;
	
	private final Asset[] assets;
	
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
	
	
	public OfflineListXMLParser(RomList romList, Asset[] assets)
	{
		this.romList = romList;
		
    format = new DecimalFormat();
    format.applyPattern("0000");
    
    this.assets = assets;
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
		  case "imageNumber":
		  {
		    for (Asset asset : assets)
		    {
		      AssetData data = rom.getAssetData(asset);
		      data.setPath(Paths.get(format.format(asInt())+".png"));
		      data.setURLData(format.format(asInt())+(asset==assets[0]?"a":"b")+".png");
		    }
		    break;
		  }
		  case "releaseNumber": rom.number = asInt(); break;
		  case "title": rom.setTitle(asString()); break;
		  case "saveType":
		  {
		    RomSave<?> save = parseSave(asString());
		    rom.setSave(save);
		    break;
		  }
		  case "romSize": rom.size = RomSize.forBytes(asLong()); break;
		  case "publisher": rom.setAttribute(RomAttribute.PUBLISHER, asString()); break;
		  case "location": rom.setAttribute(RomAttribute.LOCATION, Location.get(asInt())); break;
		  case "language":
		  {
		    int values = asInt();
		    languageMap.forEach( (k, v) -> { if ((values & k) != 0) rom.languages.add(v); });
		    break;
		  }
		  case "sourceRom": rom.setAttribute(RomAttribute.GROUP, asString()); break;
		  case "romCRC": rom.crc = Long.parseLong(asString(), 16); break;
      case "im1CRC": 
      {
        rom.getAssetData(assets[0]).setCRC(Long.parseLong(asString(), 16));
        break;
      }
      case "im2CRC": 
      {
        rom.getAssetData(assets[1]).setCRC(Long.parseLong(asString(), 16));
        break;
      }
      case "comment": rom.setAttribute(RomAttribute.COMMENT, asString()); break;
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