package jack.rm.files.parser;

import com.github.jakz.romlib.data.set.DatFormat;

import jack.rm.data.romset.GameSet;

public interface DatLoader
{
  public void load(GameSet set);
  public DatFormat getFormat();
}
