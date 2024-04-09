package jack.rm.plugins.providers;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.GameSetFeatures;
import com.github.jakz.romlib.data.set.GameSetUUID;
import com.github.jakz.romlib.data.set.Provider;
import com.github.jakz.romlib.parsers.LogiqxXMLHandler;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.xml.XMLHandler;
import com.pixbits.lib.io.xml.XMLParser;
import com.pixbits.lib.lang.StringUtils;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.GlobalSettings;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.plugins.types.DatParserPlugin;
import jack.rm.plugins.types.ProviderPlugin;

public class DatGuesserPlugin extends ProviderPlugin
{
  private static Logger logger() { return Log.getLogger(DatGuesserPlugin.class); }
  
  private static class GuessedInfo
  {
    Provider provider;
    Platform platform;
    final List<Feature> features = new ArrayList<>();
  }
  
  static class LogiqxXmlGuesser extends XMLHandler<GuessedInfo>
  {    
    enum Area { NONE, HEADER, CONFIGURATION };
    Area area = Area.NONE;
    
    Map<String, String> attributes = new HashMap<>();
        
    boolean hasAttribute(String key, String value)
    {
      return attribute(key).equals(value.toLowerCase());
    }
    
    String attribute(String key)
    {
      return attributes.getOrDefault(key, "").toLowerCase();
    }
    
    @Override
    protected void init()
    {
      
    }

    @Override
    protected void start(String ns, String name, Attributes attr) throws SAXException
    {
      if (name.equals("header"))
        area = Area.HEADER;
      else if (name.equals("configuration"))
        area = Area.CONFIGURATION;
    }

    @Override
    protected void end(String ns, String name) throws SAXException
    {
      if (name.equals("header"))
        area = Area.NONE;
      else if (name.equals("configuration"))
        area = Area.NONE;
      
      if (area == Area.HEADER)
      {
        attributes.put("header:"+name, asString());
      }
      else if (area == Area.CONFIGURATION)
      {
        attributes.put("configuration:"+name, asString());
      }
    }
    
    static private final Map<String, Platform> keywords = new LinkedHashMap<>();
    
    static
    {
      keywords.put("c64", Platforms.C64);
      keywords.put("commodore 64", Platforms.C64);
      keywords.put("amiga", Platforms.AMIGA);
      keywords.put("commodore amiga", Platforms.AMIGA);
      
      keywords.put("psp", Platforms.PSP);
      keywords.put("playstation portable", Platforms.PSP);
      keywords.put("ps2", Platforms.PS2);
      keywords.put("playstation 2", Platforms.PS2);
      keywords.put("ps1", Platforms.PS1);
      keywords.put("playstation", Platforms.PS1);
      keywords.put("psx", Platforms.PS1);

      keywords.put("n64", Platforms.N64);
      keywords.put("nintendo 64", Platforms.N64);
      keywords.put("3ds", Platforms._3DS);
      keywords.put("nintendo 3ds", Platforms._3DS);
      keywords.put("nds", Platforms.NDS);
      keywords.put("nintendo nds", Platforms.NDS);
      keywords.put("nintendo ds", Platforms.NDS);
      keywords.put("gba", Platforms.GBA);
      keywords.put("gameboy advance", Platforms.GBA);
      keywords.put("game boy advance", Platforms.GBA);
      keywords.put("gbc", Platforms.GBC);
      keywords.put("gameboy color", Platforms.GBC);
      keywords.put("game boy color", Platforms.GBC);
      keywords.put("gb", Platforms.GB);
      keywords.put("gameboy", Platforms.GB);
      keywords.put("game boy", Platforms.GB);
      
      keywords.put("snes", Platforms.SNES);
      keywords.put("super nintendo", Platforms.SNES);
      keywords.put("nes", Platforms.NES);
      keywords.put("nintendo", Platforms.NES);
      
      keywords.put("ibm", Platforms.IBM_PC);
      keywords.put("pc", Platforms.IBM_PC);
      keywords.put("good old days", Platforms.IBM_PC);

      keywords.put("wonderswan", Platforms.WS);
      
      keywords.put("a2600", Platforms.A2600);
      keywords.put("atari 2600", Platforms.A2600);
      keywords.put("2600", Platforms.A2600);

      keywords.put("ds", Platforms.NDS);

      //TODO: color
    }
    
    private Platform guessPlatform()
    {
      Platform platform = null;
      
      String name = attribute("header:name");
      
      if (name.isEmpty())
        name = attribute("configuration:system");
      
      for (Map.Entry<String, Platform> entry : keywords.entrySet())
      {
        if (name.contains(entry.getKey()))
        {
          platform = entry.getValue();
          break;
        }
      }
      
      /* 
      int distance = Integer.MAX_VALUE;

      for (Platform p : Platforms.values())
      {
        int current = StringUtils.levenshteinDistance(p.getName(), name);
        
        if (current < distance)
        {
          distance = current;
          platform = p;
        }
        
        current = StringUtils.levenshteinDistance(p.getTag(), name);
        
        if (current < distance)
        {
          distance = current;
          platform = p;
        }
      }
      */
      
      if (platform == null)
        logger().w("unknown platform for dat: %s", name);
      
      /*if (platform == null)
      {
        logger().w("unknown platform for dat");
        attributes.forEach((k, v) -> System.out.println(k+" > "+v));
      }*/
      
      return platform;
    }
    
    private Provider guessProvider()
    {
      Provider provider = null;
      
      if (hasAttribute("header:homepage", "redump.org"))
        provider = KnownProviders.REDUMP;
      else if (hasAttribute("header:homepage", "No-Intro"))
        provider = KnownProviders.NO_INTRO;
      else if (hasAttribute("header:url", "https://www.no-intro.org"))
        provider = KnownProviders.NO_INTRO;
      else if (hasAttribute("header:name", "The Good Old Days"))
        provider = KnownProviders.GOOD_OLD_DAYS;
      else if (attribute("configuration:datName").contains("advanscene"))
        provider = KnownProviders.ADVAN_SCENE;
      else if (attribute("configuration:datName").contains("no-intro"))
        provider = KnownProviders.NO_INTRO;
      else if (attribute("configuration:datName").contains("offlinelist"))
        provider = KnownProviders.OFFLINE_LIST;
      else
      {
        logger().w("unknown provider for dat");
        attributes.forEach((k, v) -> System.out.println(k+" > "+v));
      }
      
      return provider;
    }

    @Override
    public GuessedInfo get()
    {
      GuessedInfo info = new GuessedInfo();
      
      info.provider = guessProvider();
      info.platform = guessPlatform();
      
      
      return info;     
    }
    
  }
  
  
  @Override
  public GameSet[] buildRomSets(List<DatParserPlugin> datParsers)
  {
    List<GameSet> sets = new ArrayList<>();

    try
    {
      if (!Files.exists(GlobalSettings.DAT_PATH))
        return new GameSet[0];
      
      Files.newDirectoryStream(GlobalSettings.DAT_PATH).forEach(StreamException.rethrowConsumer(path -> {
        logger().d("found dat file %s", path.toString());
        
        try
        {
          LogiqxXmlGuesser xmlParser = new LogiqxXmlGuesser();
          
          XMLParser<GuessedInfo> parser = new XMLParser<>(xmlParser);
          
          GuessedInfo data = parser.load(path);
          
          if (data.platform != null && data.provider != null)
          {
            DataSupplier datParser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml"); 
            DatFormat format = datParser.getFormat();
            
            List<Attribute> attributes = new ArrayList<>(List.of(
              GameAttribute.TITLE,
              GameAttribute.SIZE,
              GameAttribute.LOCATION,
              GameAttribute.LANGUAGE,
              GameAttribute.VERSION,
              GameAttribute.COMMENT
                
            ));

            GameSet set = new GameSet(
                data.platform,
                data.provider,
                datParser,
                format,
                attributes.toArray(new Attribute[attributes.size()]),
                AssetManager.DUMMY, // AssetManager assetManager,
                s -> new MyGameSetFeatures(s, Feature.FINITE_SIZE_SET) // Function<GameSet, GameSetFeatures> helper
            );
            
            set.setUUID(new GameSetUUID(String.format("%08X", path.toString().hashCode())));
            set.setDatPath(path);
            
            sets.add(set);
          }
        }
        catch (SAXException e)
        {
          // skipping invalid dat
        }
      }));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
        
    return sets.toArray(new GameSet[sets.size()]);
  }
}
