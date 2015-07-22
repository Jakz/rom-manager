package jack.rm.data.parser;

import jack.rm.data.set.RomSet;

@FunctionalInterface
public interface DatLoader
{
  public void load(RomSet set);
}
