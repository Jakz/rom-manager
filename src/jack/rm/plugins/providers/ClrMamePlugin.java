package jack.rm.plugins.providers;

import java.util.List;

import com.github.jakz.romlib.data.set.Provider;

import jack.rm.assets.EmptyAssetManager;
import jack.rm.data.console.System;
import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.datparsers.DatParserPlugin;


public class ClrMamePlugin extends ProviderPlugin
{
  private final static Attribute[] GG_ATTRIBUTES = 
  {
    RomAttribute.TITLE,
    RomAttribute.LOCATION,
    RomAttribute.LANGUAGE,
    RomAttribute.CRC,
    RomAttribute.MD5,
    RomAttribute.SHA1,
    RomAttribute.VERSION,
    RomAttribute.COMMENT,
    RomAttribute.SIZE,
  };
  
  @Override
  public RomSet[] buildRomSets(List<DatParserPlugin> datParsers)
  {
    DatParserPlugin datParser = this.findDatParser(datParsers, "clr-mame-nointro");
    
    RomSet[] sets = new RomSet[2];
    
    Provider.Source gameGearSource = new Provider.Source("http://datomatic.no-intro.org/?page=download&fun=dat",
      "inc_unl", "1",
      "format", "clrmamepro",
      "language_filter", "all_languages",
      "region_filter", "all_regions",
      "dbutton", "Download",
      "Download27", ""
    );

    sets[0] = new RomSet(
        System.GG, 
        KnownProviders.NO_INTRO.derive(null, null, "", "", null), 
        GG_ATTRIBUTES, 
        new EmptyAssetManager(), 
        datParser.buildDatLoader("clr-mame-nointro")
    );
    
    sets[1] = new RomSet(
        System.LYNX, 
        KnownProviders.NO_INTRO.derive(null, null, "", "", null), 
        GG_ATTRIBUTES, 
        new EmptyAssetManager(), 
        datParser.buildDatLoader("clr-mame-nointro")
    );

    return sets;
  }
}
