package jack.rm.files.parser;

import com.github.jakz.romlib.data.set.DatFormat;

import jack.rm.data.romset.RomSet;

public interface DatLoader
{
  public void load(RomSet set);
  public DatFormat getFormat();
}
