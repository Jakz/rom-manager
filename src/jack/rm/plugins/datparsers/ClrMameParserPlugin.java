package jack.rm.plugins.datparsers;

import java.util.Map;

import com.github.jakz.romlib.data.set.DataSupplier;

import com.github.jakz.romlib.parsers.ClrMameProParser;
import com.github.jakz.romlib.parsers.cataloguers.NoIntroCataloguer;

public class ClrMameParserPlugin extends DatParserPlugin
{
  @Override public String[] getSupportedFormats() { return new String[] {"clr-mame", "clr-mame-nointro"}; }
  
  @Override
  public DataSupplier buildDatLoader(String format, Map<String, Object> arguments)
  {
    /* TODO: use arguments to pass cataloguer directly to dat loader? */
    
    if (format.equals("clr-mame"))
      return new ClrMameProParser();
    else if (format.equals("clr-mame-nointro"))
      return DataSupplier.derive(new ClrMameProParser(), new NoIntroCataloguer());
    else
      return null;
  }
}
