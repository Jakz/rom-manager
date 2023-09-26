package jack.rm.plugins.datparsers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.cataloguers.impl.NoIntroCataloguer;
import com.github.jakz.romlib.data.cataloguers.impl.NoIntroNormalizer;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.parsers.LogiqxXMLHandler;
import com.github.jakz.romlib.parsers.XMDBHandler;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.xml.XMLParser;

import jack.rm.plugins.types.DatParserPlugin;

public class LogiqxXmlParserPlugin extends DatParserPlugin
{
  private final DatFormat format = DatFormat.of("logiqx-xml", "lx", "xml");
  
  
  @Override
  public String[] getSupportedFormats()
  {
    return new String[] { format.getLongIdentifier() };
  }

  @Override
  public DataSupplier buildDatLoader(String format, Map<String, Object> arguments)
  {
    if (format.equals(this.format.getLongIdentifier()))
      return new LogiqxXmlSupplier().apply(new NoIntroCataloguer()).apply(new NoIntroNormalizer());
    else
      return null;
  }
  
  class LogiqxXmlSupplier implements DataSupplier
  {
    @Override
    public DataSupplier.Data load(final GameSet set)
    {
      //TODO: probably exceptions should be catched by loader not here
      try
      {
        LogiqxXMLHandler xmlParser = new LogiqxXMLHandler();
        xmlParser.setGameFactory(() -> new Game(set));
        xmlParser.initSizeSet(set.hasFeature(Feature.FINITE_SIZE_SET));

        XMLParser<LogiqxXMLHandler.Data> parser = new XMLParser<>(xmlParser);
        LogiqxXMLHandler.Data data = parser.load(set.datPath());
        
        CloneSet clones = null;
        
        /* if data has clone information use it */
        if (data.hasClones())
        {
          HashMap<Game, List<Game>> cloneMap = new HashMap<>();
          
          for (Map.Entry<String, String> info : data.childToParentCloneMap.entrySet())
          {
            Game parent = data.list.get(info.getValue());
            Game child = data.list.get(info.getKey());
            
            if (child != null && parent != null)
            {
              cloneMap.compute(parent, (k, v) -> {
                if (v != null)
                {
                  v.add(child);
                  return v;
                }
                else
                {
                  v = new ArrayList<>();
                  v.add(parent);
                  v.add(child);
                  return v;
                }
              });
            }
            
            GameClone[] cloneList = cloneMap.entrySet().stream()
            .map(e ->  new GameClone(e.getValue()))
            .collect(Collectors.toList())
            .toArray(new GameClone[cloneMap.size()]);
            
            clones = new CloneSet(cloneList);
          }
        }
        
        /* if clone file is present override information */
        //TODO: maybe it should merge with previous?
        Path xmdbPath = Paths.get(FileUtils.trimExtension(set.datPath().toString()) + ".xmdb");  
        if (Files.exists(xmdbPath))
        {
          clones = XMDBHandler.loadCloneSet(data.list, xmdbPath);
        }

        return new DataSupplier.Data(data.list, clones);

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      
      
      return null;
    }

    @Override public DatFormat getFormat() { return format; }  
  }

}
