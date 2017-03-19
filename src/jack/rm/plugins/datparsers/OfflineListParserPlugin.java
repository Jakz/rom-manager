package jack.rm.plugins.datparsers;

import java.io.CharArrayWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UnknownFormatConversionException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.RomSave;
import com.github.jakz.romlib.data.set.DatFormat;

import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomGroup;
import jack.rm.data.rom.RomGroupID;
import jack.rm.data.rom.RomSize;
import jack.rm.data.romset.RomSet;
import jack.rm.files.parser.DatLoader;
import jack.rm.files.parser.SaveParser;
import jack.rm.files.parser.XMLDatLoader;
import jack.rm.files.parser.XMLHandler;

public class OfflineListParserPlugin extends DatParserPlugin
{
  static final private Map<Integer, Language> languageMap = new HashMap<>();
  static final private Map<Integer, Location> locationMap = new HashMap<>();
  
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
    
    locationMap.put(0,Location.EUROPE);
    locationMap.put(1,Location.USA);
    locationMap.put(2,Location.GERMANY);
    locationMap.put(3,Location.CHINA);
    locationMap.put(4,Location.SPAIN);
    locationMap.put(5,Location.FRANCE);
    locationMap.put(6,Location.ITALY);
    locationMap.put(7,Location.JAPAN);
    locationMap.put(8,Location.NETHERLANDS);
    locationMap.put(19,Location.AUSTRALIA);
    locationMap.put(22,Location.KOREA);
    locationMap.put(16,Location.JAPAN);
    locationMap.put(18,Location.JAPAN);
  }
  
  class OfflineListXMLDatLoader extends XMLDatLoader
  {
    protected OfflineListXMLDatLoader(XMLHandler handler) { super(handler); }
    @Override public DatFormat getFormat() { return new DatFormat("ol", "xml"); }
  }
  
  public class OfflineListXMLParser extends XMLHandler
  {
    CharArrayWriter buffer = new CharArrayWriter();
    
    private final DecimalFormat format;
    
    private Asset[] assets;
   
    
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
      String value = asString();
      return !value.isEmpty() ? Integer.parseInt(asString()) : 0;
    }
    
    public long asLong()
    {
      String value = asString();
      return !value.isEmpty() ? Long.parseLong(asString()) : 0;
    }
    
    public long asHexLong()
    {
      String value = asString();
      return !value.isEmpty() ? Long.parseLong(asString(), 16) : 0;
    }

    Map<String, RomSave<?>> saves = new TreeMap<>();
    
    RomGroup getGroup(int id)
    {
      return set.list.getGroup(new RomGroupID(id));
    }
    
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
          rom.setAttribute(RomAttribute.IMAGE_NUMBER, asInt());
          break;
        }
        case "releaseNumber": rom.setAttribute(RomAttribute.NUMBER, asInt()); break;
        case "title": rom.setTitle(asString()); break;
        case "saveType":
        {       
          try
          {
            RomSave<?> save = saveParser.parse(asString());
            //saves.put(asString(), save);
            rom.setAttribute(RomAttribute.SAVE_TYPE, save);
          }
          catch (UnknownFormatConversionException e)
          {
            System.out.println("Rom: "+rom.getTitle());
            e.printStackTrace();
          }
          break;
        }
        case "romSize": rom.setSize(RomSize.forBytes(asLong())); break;
        case "publisher": rom.setAttribute(RomAttribute.PUBLISHER, asString()); break;
        case "location":
        {
          rom.setAttribute(RomAttribute.LOCATION, locationMap.getOrDefault(asInt(), Location.NONE)); break;
        }
        case "language":
        {
          int values = asInt();
          languageMap.forEach( (k, v) -> { if ((values & k) != 0) rom.getLanguages().add(v); });
          break;
        }
        case "sourceRom": rom.setAttribute(RomAttribute.GROUP, asString()); break;
        case "romCRC": rom.setCRC(asHexLong()); break;
        case "im1CRC": 
        {
          rom.getAssetData(assets[0]).setCRC(asHexLong());
          break;
        }
        case "im2CRC": 
        {
          rom.getAssetData(assets[1]).setCRC(asHexLong());
          break;
        }
        case "duplicateID":
        {
          int ident = asInt();
          
          if (ident != 0)
          {
            RomGroup group = getGroup(ident);
            rom.addToGroup(group);
          }
          
          break;
        }
        case "comment": rom.setAttribute(RomAttribute.COMMENT, asString()); break;
        case "game": set.list.add(rom); break;
        case "games":
        {
          set.list.precomputeCache(); 
          saves.forEach((k,v) -> System.out.println(k+" -> "+v));
          
          System.out.println("Groups: "+set.list.groupsCount());
          
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
  
  
  @Override public String[] getSupportedFormats() { return new String[] { "offline-list" }; }


  @Override
  public DatLoader buildDatLoader(String format, Map<String, Object> arguments)
  {
    checkArgument(arguments, "save-parser", SaveParser.class);

    if (format.equals("offline-list"))
    {
      return new OfflineListXMLDatLoader(new OfflineListXMLParser((SaveParser)arguments.get("save-parser")));
    }
    else
      return null;
  }

}
