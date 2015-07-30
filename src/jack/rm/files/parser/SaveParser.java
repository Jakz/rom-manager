package jack.rm.files.parser;

import jack.rm.data.rom.RomSave;

@FunctionalInterface
public interface SaveParser
{
  public RomSave<?> parse(String string);
}
