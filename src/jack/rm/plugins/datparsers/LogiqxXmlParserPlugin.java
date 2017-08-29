package jack.rm.plugins.datparsers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameList;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.parsers.LogiqxXMLParser;
import com.github.jakz.romlib.parsers.XMDBParser;
import com.github.jakz.romlib.parsers.cataloguers.NoIntroCataloguer;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.xml.XMLParser;

public class LogiqxXmlParserPlugin extends DatParserPlugin
{

  @Override
  public String[] getSupportedFormats()
  {
    return new String[] { "logiqx-xml" };
  }

  @Override
  public DataSupplier buildDatLoader(String format, Map<String, Object> arguments)
  {
    return DataSupplier.derive(new LogiqxXmlSupplier(), new NoIntroCataloguer());
  }
  
  class LogiqxXmlSupplier implements DataSupplier
  {
    @Override
    public DataSupplier.Data load(final GameSet set)
    {
      //TODO: probably exceptions should be catched by loader not here
      try
      {
        LogiqxXMLParser xmlParser = new LogiqxXMLParser();
        xmlParser.setGameFactory(() -> new Game(set));
        xmlParser.initSizeSet(set.hasFeature(Feature.FINITE_SIZE_SET));

        
        XMLParser<LogiqxXMLParser.Data> parser = new XMLParser<>(xmlParser);
        LogiqxXMLParser.Data data = parser.load(set.datPath());
        
        Path xmdbPath = Paths.get(FileUtils.trimExtension(set.datPath().toString()) + ".xmdb");  
        CloneSet clones = XMDBParser.loadCloneSet(data.list, xmdbPath);
        
        return new DataSupplier.Data(data.list, clones);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      
      
      return null;
    }

    @Override public DatFormat getFormat() { return new DatFormat("lx", "xml"); }    
  }

}
