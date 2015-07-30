package jack.rm.files.parser;

import jack.rm.data.romset.RomSet;

@FunctionalInterface
public interface DatLoader
{
  public void load(RomSet set);
}
