package jack.rm.plugins.providers;

import java.util.List;

import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.Provider;

import jack.rm.plugins.datparsers.DatParserPlugin;


public class ClrMamePlugin extends ProviderPlugin
{
  private final static Attribute[] GG_ATTRIBUTES = 
  {
    GameAttribute.TITLE,
    GameAttribute.LOCATION,
    GameAttribute.LANGUAGE,
    GameAttribute.VERSION,
    GameAttribute.COMMENT,
  };
  
  @Override
  public GameSet[] buildRomSets(List<DatParserPlugin> datParsers)
  {
    DatParserPlugin datParser = this.findDatParser(datParsers, "clr-mame-nointro");

    GameSet[] sets = new GameSet[2];
    
    Provider.Source gameGearSource = new Provider.Source("http://datomatic.no-intro.org/?page=download&fun=dat",
      "inc_unl", "1",
      "format", "clrmamepro",
      "language_filter", "all_languages",
      "region_filter", "all_regions",
      "dbutton", "Download",
      "Download27", ""
    );

    {
      DataSupplier parser = datParser.buildDatLoader("clr-mame-nointro"); 
      DatFormat format = parser.getFormat();
          
      sets[0] = new GameSet(
          Platform.GG, 
          KnownProviders.NO_INTRO.derive("", "", "", null), 
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY
      );
    }
    
    {
      DataSupplier parser = datParser.buildDatLoader("clr-mame-nointro"); 
      DatFormat format = parser.getFormat();
      
      sets[1] = new GameSet(
          Platform.LYNX, 
          KnownProviders.NO_INTRO.derive("", "", "", null),
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY
      );
    }
    
    return sets;
  }
}
