package jack.rm.plugins.providers;

import java.util.List;

import jack.rm.assets.EmptyAssetManager;
import jack.rm.data.console.System;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.Attribute;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.datparsers.DatParserPlugin;
import jack.rm.plugins.providers.*;


public class ClrMamePlugin extends ProviderPlugin
{
  private final static Attribute[] GG_ATTRIBUTES = 
  {
    RomAttribute.TITLE,
    RomAttribute.CRC,
    RomAttribute.SIZE,
  };
  
  @Override
  public RomSet buildRomSet(List<DatParserPlugin> datParsers, System system)
  {
    DatParserPlugin datParser = this.findDatParser(datParsers, "clr-mame");

    if (system == System.GG)
    {
      RomSet romSet = new RomSet(
          system, 
          KnownProviders.NO_INTRO, 
          GG_ATTRIBUTES, 
          new EmptyAssetManager(), 
          datParser.buildDatLoader("clr-mame")
      );
      return romSet;
    }

    return null;
  }

  @Override public boolean isSystemSupported(System system)
  {
    return system == System.GG;
  }

}
