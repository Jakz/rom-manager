package jack.rm.plugins.providers.offlinelist;

import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.*;
import org.xml.sax.*;
import java.io.CharArrayWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jack.rm.data.parser.SaveParser;
import jack.rm.data.parser.XMLHandler;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;

public class OfflineListXMLParser extends XMLHandler
{
	CharArrayWriter buffer = new CharArrayWriter();
	
	private final DecimalFormat format;
	
	private Asset[] assets;
	
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
	
	Rom rom;
	int curString;
	
	private SaveParser saveParser;
	
	boolean started = false;
	
	public void setRomSet(RomSet set)
	{
	  super.setRomSet(set);
	  this.assets = set.getAssetManager().getSupportedAssets();
	}
	
	public OfflineListXMLParser(SaveParser saveParser)
	{
    format = new DecimalFormat();
    format.applyPattern("0000");
    this.saveParser = saveParser;
	}

	@Override
  public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException
	{			
		if (localName.equals("game"))
		{
			rom = new Rom(set);
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
		      data.setURLData(asInt()+(asset==assets[0]?"a":"b")+".png");
		    }
		    break;
		  }
		  case "releaseNumber": rom.setAttribute(RomAttribute.NUMBER, asInt()); break;
		  case "title": rom.setTitle(asString()); break;
		  case "saveType":
		  {
		    RomSave<?> save = saveParser.parse(asString());
		    rom.setAttribute(RomAttribute.SAVE_TYPE, save);
		    break;
		  }
		  case "romSize": rom.setSize(RomSize.forBytes(asLong())); break;
		  case "publisher": rom.setAttribute(RomAttribute.PUBLISHER, asString()); break;
		  case "location":
		  {
		    rom.setAttribute(RomAttribute.LOCATION, Location.get(asInt())); break;
		  }
		  case "language":
		  {
		    int values = asInt();
		    languageMap.forEach( (k, v) -> { if ((values & k) != 0) rom.getLanguages().add(v); });
		    break;
		  }
		  case "sourceRom": rom.setAttribute(RomAttribute.GROUP, asString()); break;
		  case "romCRC": rom.setCRC(Long.parseLong(asString(), 16)); break;
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
      case "game": set.list.add(rom); break;
      case "games":
      {
        set.list.sort(); 
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