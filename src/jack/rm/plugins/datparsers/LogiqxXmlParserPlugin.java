package jack.rm.plugins.datparsers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.github.jakz.romlib.data.cataloguers.NoIntroCataloguer;
import com.github.jakz.romlib.data.cataloguers.NoIntroNormalizer;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameList;
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
      return DataSupplier.derive(new LogiqxXmlSupplier(), new NoIntroCataloguer(), new NoIntroNormalizer());
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
        
        Path xmdbPath = Paths.get(FileUtils.trimExtension(set.datPath().toString()) + ".xmdb");  
        CloneSet clones = XMDBHandler.loadCloneSet(data.list, xmdbPath);
        
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
