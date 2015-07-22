package jack.rm.data.parser;

import jack.rm.data.RomSave;

@FunctionalInterface
public interface SaveParser
{
  public RomSave<?> parse(String string);
}
