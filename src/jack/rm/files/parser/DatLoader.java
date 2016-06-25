package jack.rm.files.parser;

import jack.rm.data.romset.DatFormat;
import jack.rm.data.romset.RomSet;

public interface DatLoader
{
  public void load(RomSet set);
  public DatFormat getFormat();
}
