package jack.rm.plugins.datparsers;

import java.util.Map;

import com.github.jakz.romlib.data.cataloguers.NoIntroCataloguer;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.parsers.ClrMameProParser;

import jack.rm.plugins.types.DatParserPlugin;

public class ClrMameParserPlugin extends DatParserPlugin
{
  @Override public String[] getSupportedFormats() { return new String[] {"clr-mame-pro", "clr-mame-pro-nointro"}; }
  
  @Override
  public DataSupplier buildDatLoader(String format, Map<String, Object> arguments)
  {
    /* TODO: use arguments to pass cataloguer directly to dat loader? */
    
    if (format.equals("clr-mame-pro"))
      return new ClrMameProParser();
    else if (format.equals("clr-mame-pro-nointro"))
      return DataSupplier.derive(new ClrMameProParser(), new NoIntroCataloguer());
    else
      return null;
  }
}
