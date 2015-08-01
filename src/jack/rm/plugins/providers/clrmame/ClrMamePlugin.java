package jack.rm.plugins.providers.clrmame;

import jack.rm.assets.EmptyAssetManager;
import jack.rm.data.console.System;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.Attribute;
import jack.rm.data.romset.RomSet;
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
  public RomSet buildRomSet(System system)
  {
    if (system == System.GG)
    {
      RomSet romSet = new RomSet(
          system, 
          new NoIntroProvider(), 
          new ClrMameProviderType(),
          GG_ATTRIBUTES, 
          new EmptyAssetManager(), 
          new ClrMameParser()
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
