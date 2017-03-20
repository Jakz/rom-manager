package jack.rm.plugins.datparsers;

import java.io.CharArrayWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UnknownFormatConversionException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.GameSize;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameSave;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.DatFormat;

import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.romset.GameSet;
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
   
    
    Game rom;
    int curString;
    
    private SaveParser saveParser;
    
    boolean started = false;
    
    public void setRomSet(GameSet set)
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
        rom = new Game(set);
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

    Map<String, GameSave<?>> saves = new TreeMap<>();
    Map<Integer, Set<Game>> clones = new HashMap<>();
    
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
          rom.setAttribute(GameAttribute.IMAGE_NUMBER, asInt());
          break;
        }
        case "releaseNumber": rom.setAttribute(GameAttribute.NUMBER, asInt()); break;
        case "title": rom.setTitle(asString()); break;
        case "saveType":
        {       
          try
          {
            GameSave<?> save = saveParser.parse(asString());
            //saves.put(asString(), save);
            rom.setAttribute(GameAttribute.SAVE_TYPE, save);
          }
          catch (UnknownFormatConversionException e)
          {
            System.out.println("Rom: "+rom.getTitle());
            e.printStackTrace();
          }
          break;
        }
        case "romSize": rom.setSize(set.sizeSet.forBytes(asLong())); break;
        case "publisher": rom.setAttribute(GameAttribute.PUBLISHER, asString()); break;
        case "location":
        {
          rom.setAttribute(GameAttribute.LOCATION, locationMap.getOrDefault(asInt(), Location.NONE)); break;
        }
        case "language":
        {
          int values = asInt();
          languageMap.forEach( (k, v) -> { if ((values & k) != 0) rom.getLanguages().add(v); });
          break;
        }
        case "sourceRom": rom.setAttribute(GameAttribute.GROUP, asString()); break;
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
          
          Set<Game> currentClones = clones.putIfAbsent(ident, new HashSet<Game>());
          currentClones.add(rom);

          break;
        }
        case "comment": rom.setAttribute(GameAttribute.COMMENT, asString()); break;
        case "game": set.list.add(rom); break;
        case "games":
        {
          set.list.precomputeCache(); 
          saves.forEach((k,v) -> System.out.println(k+" -> "+v));
          
          // TODO: check clones for elements with .size() > 1 and add to CloneSet
          
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
