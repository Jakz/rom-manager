package jack.rm.plugins.datparsers;

import java.io.CharArrayWriter;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.GameSave;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameList;
import com.github.jakz.romlib.data.set.GameSet;

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
    private final CharArrayWriter buffer = new CharArrayWriter();

    private Game game;
    long crc = -1;
    RomSize size = null;
    RomSize.Set sizeSet;
    private List<Game> games = new ArrayList<>();
    private Map<String, GameSave<?>> saves = new TreeMap<>();
    private Map<Integer, Set<Game>> clones = new HashMap<>();
    
    private final DecimalFormat format;
    private SaveParser saveParser;

    private Asset[] assets;

    boolean started = false;
    
    public void setRomSet(GameSet set)
    {
      super.setRomSet(set);
      this.sizeSet = set.hasFeature(Feature.FINITE_SIZE_SET) ? new RomSize.RealSet() : new RomSize.NullSet();
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
        game = new Game(set);
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
            AssetData data = game.getAssetData(asset);
            data.setPath(Paths.get(format.format(asInt())+".png"));
            data.setURLData(asInt()+(asset==assets[0]?"a":"b")+".png");
          }
          game.setAttribute(GameAttribute.IMAGE_NUMBER, asInt());
          break;
        }
        case "releaseNumber": game.setAttribute(GameAttribute.NUMBER, asInt()); break;
        case "title": game.setTitle(asString()); break;
        case "saveType":
        {       
          try
          {
            GameSave<?> save = saveParser.parse(asString());
            //saves.put(asString(), save);
            game.setAttribute(GameAttribute.SAVE_TYPE, save);
          }
          catch (UnknownFormatConversionException e)
          {
            System.out.println("Rom: "+game.getTitle());
            e.printStackTrace();
          }
          break;
        }
        case "publisher": game.setAttribute(GameAttribute.PUBLISHER, asString()); break;
        case "location":
        {
          Location location = locationMap.get(asInt());
          
          if (location != null)
            game.getLocation().set(location);
          
          break;
        }
        case "language":
        {
          int values = asInt();
          
          languageMap.forEach( (k, v) -> { if ((values & k) != 0) game.getLanguages().add(v); });
          break;
        }
        case "sourceRom": game.setAttribute(GameAttribute.GROUP, asString()); break;
        case "romCRC":
        {
          crc = asHexLong();
          break;
        }
        case "romSize":
        {
          size = sizeSet.forBytes(asLong());
          break;
        }
        case "im1CRC": 
        {
          game.getAssetData(assets[0]).setCRC(asHexLong());
          break;
        }
        case "im2CRC": 
        {
          game.getAssetData(assets[1]).setCRC(asHexLong());
          break;
        }
        case "duplicateID":
        {
          int ident = asInt();
          
          Set<Game> currentClones = clones.computeIfAbsent(ident, i -> new HashSet<>());
          currentClones.add(game);

          break;
        }
        case "comment": game.setAttribute(GameAttribute.COMMENT, asString()); break;
        case "game":
        {
          if (crc == -1 || size == null)
          {
            //TODO: throw fail exception
          }
          
          Rom rom = new Rom(size, crc);
          game.setRom(rom);
          games.add(game); break;
        }
        case "games":
        { 
          break;
        }
        

      }
    }
    
    @Override public DataSupplier.Data get()
    {
      saves.forEach((k,v) -> System.out.println(k+" -> "+v));

      GameList list = new GameList(games, sizeSet);
      CloneSet cloneSet = null;
      
      List<GameClone> clones = this.clones.values().stream()
          .filter(s -> s.size() > 1)
          .map(g -> new GameClone(g))
          .collect(Collectors.toList());
        
      if (!clones.isEmpty())
      {
        cloneSet = new CloneSet(clones.toArray(new GameClone[clones.size()]));
        System.out.println("Clones: "+cloneSet.size());
      }
      
      return new DataSupplier.Data(list, cloneSet);
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
    {
      buffer.write(ch,start,length);
    }
  }
  
  
  @Override public String[] getSupportedFormats() { return new String[] { "offline-list" }; }


  @Override
  public DataSupplier buildDatLoader(String format, Map<String, Object> arguments)
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
