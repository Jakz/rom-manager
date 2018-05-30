package jack.rm.plugins.providers;

import java.util.ArrayList;
import java.util.List;

import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.cataloguers.impl.GoodOldDaysFixer;
import com.github.jakz.romlib.data.cataloguers.impl.NormalizedTitleCloneSetCreator;
import com.github.jakz.romlib.data.cataloguers.impl.RedumpAggregatorByDisks;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.Provider;

import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.plugins.types.DatParserPlugin;
import jack.rm.plugins.types.ProviderPlugin;


public class ClrMamePlugin extends ProviderPlugin
{
  private final static Attribute[] GG_ATTRIBUTES = 
  {
    GameAttribute.TITLE,
    GameAttribute.SIZE,
    GameAttribute.LOCATION,
    GameAttribute.LANGUAGE,
    GameAttribute.VERSION,
    GameAttribute.COMMENT,
  };
  
  @Override
  public GameSet[] buildRomSets(List<DatParserPlugin> datParsers)
  {
    DatParserPlugin datParser = this.findDatParser(datParsers, "clr-mame-pro-nointro");

    List<GameSet> sets = new ArrayList<>();
    
    Provider.Source gameGearSource = new Provider.Source("http://datomatic.no-intro.org/?page=download&fun=dat",
      "inc_unl", "1",
      "format", "clrmamepro",
      "language_filter", "all_languages",
      "region_filter", "all_regions",
      "dbutton", "Download",
      "Download27", ""
    );

    {
      DataSupplier parser = datParser.buildDatLoader("clr-mame-pro-nointro"); 
      DatFormat format = parser.getFormat();
          
      sets.add(new GameSet(
          Platforms.GG, 
          KnownProviders.NO_INTRO.derive("", "", "", null), 
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s, Feature.FINITE_SIZE_SET)
      ));
    }
    
    {
      DataSupplier parser = datParser.buildDatLoader("clr-mame-pro-nointro"); 
      DatFormat format = parser.getFormat();
      
      sets.add(new GameSet(
          Platforms.LYNX, 
          KnownProviders.NO_INTRO.derive("", "", "", null),
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s, Feature.FINITE_SIZE_SET)
      ));
    }
    
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml"); 
      DatFormat format = parser.getFormat();
      
      sets.add(new GameSet(
          Platforms.PSP, 
          KnownProviders.NO_INTRO.derive("", "", "", null),
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s)
      ));
    }
        
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml"); 
      DatFormat format = parser.getFormat();
      
      sets.add(new GameSet(
          Platforms.PSP, 
          KnownProviders.NO_INTRO.derive("With Clones", "with-clones", "", null),
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s)
      ));
    }
    
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml"); 
      DatFormat format = parser.getFormat();
      
      sets.add(new GameSet(
          Platforms.N64, 
          KnownProviders.NO_INTRO.derive("", "", "", null),
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s)
      ));
    }
    
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml"); 
      DatFormat format = parser.getFormat();
      
      sets.add(new GameSet(
          Platforms._3DS, 
          KnownProviders.NO_INTRO.derive("", "", "", null),
          parser,
          format,
          GG_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s)
      ));
    }
    
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml");
      parser = DataSupplier.derive(parser, new RedumpAggregatorByDisks());
      parser = DataSupplier.derive(parser, new NormalizedTitleCloneSetCreator()); 
      DatFormat format = parser.getFormat();
      
      final Attribute[] PC_ATTRIBUTES = 
        {
          GameAttribute.TITLE,
          GameAttribute.SIZE,
          GameAttribute.LOCATION,
          GameAttribute.LANGUAGE,
          GameAttribute.VERSION,
          GameAttribute.VERSION_NUMBER,
        };
      
      sets.add(new GameSet(
          Platforms.IBM_PC, 
          KnownProviders.REDUMP.derive("", "", "", null),
          parser,
          format,
          PC_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s)
      ));
    }
    
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml");
      parser = DataSupplier.derive(parser, new RedumpAggregatorByDisks());
      parser = DataSupplier.derive(parser, new NormalizedTitleCloneSetCreator()); 
      DatFormat format = parser.getFormat();
      
      final Attribute[] PC_ATTRIBUTES = 
        {
          GameAttribute.TITLE,
          GameAttribute.SIZE,
          GameAttribute.LOCATION,
          GameAttribute.LANGUAGE,
          GameAttribute.VERSION,
          GameAttribute.VERSION_NUMBER,
        };
      
      sets.add(new GameSet(
          Platforms.PS2, 
          KnownProviders.REDUMP.derive("", "", "", null),
          parser,
          format,
          PC_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s)
      ));
    }
    
    {
      DataSupplier parser = findDatParser(datParsers, "logiqx-xml").buildDatLoader("logiqx-xml");
      parser = DataSupplier.derive(parser, new GoodOldDaysFixer(), t -> t);
      parser = DataSupplier.derive(parser, new GoodOldDaysFixer());
      DatFormat format = parser.getFormat();

      final Attribute[] PC_ATTRIBUTES = 
        {
          GameAttribute.TITLE,
          GameAttribute.SIZE,
          GameAttribute.LOCATION,
          GameAttribute.LANGUAGE,
          GameAttribute.RELEASE_DATE,
          GameAttribute.COMMENT
        };
      
      sets.add(new GameSet(
          Platforms.IBM_PC, 
          KnownProviders.GOOD_OLD_DAYS,
          parser,
          format,
          PC_ATTRIBUTES, 
          AssetManager.DUMMY,
          s -> new MyGameSetFeatures(s, GameID.Generator.BY_RELEASE_NUMBER, Feature.FINITE_SIZE_SET)
      ));
    }
    
    return sets.toArray(new GameSet[sets.size()]);
  }
}
