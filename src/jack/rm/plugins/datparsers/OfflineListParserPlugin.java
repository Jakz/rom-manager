package jack.rm.plugins.datparsers;

import java.util.Map;

import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.parsers.OfflineListXMLHandler;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.xml.XMLParser;

import jack.rm.files.parser.SaveParser;
import jack.rm.plugins.types.DatParserPlugin;

public class OfflineListParserPlugin extends DatParserPlugin
{  
  private final DatFormat format = DatFormat.of("offline-list", "ol", "xml");
  
  @Override public String[] getSupportedFormats() { return new String[] { format.getLongIdentifier() }; }

  @Override
  public DataSupplier buildDatLoader(String format, Map<String, Object> arguments)
  {
    checkArgument(arguments, "save-parser", SaveParser.class);

    if (format.equals(this.format.getLongIdentifier()))
    {
      return DataSupplier.of(
      this.format,
      StreamException.rethrowFunction(set -> {
        OfflineListXMLHandler xmlParser = new OfflineListXMLHandler((SaveParser)arguments.get("save-parser"));
        xmlParser.setRomSet(set);
        //xmlParser.setGameFactory(() -> new Game(set));
        //xmlParser.initSizeSet(set.hasFeature(Feature.FINITE_SIZE_SET));

        XMLParser<DataSupplier.Data> parser = new XMLParser<>(xmlParser);
        DataSupplier.Data data = parser.load(set.datPath());
        
        return data;
      }));
    }
    else
      return null;
  }

}
