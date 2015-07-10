package jack.rm.data;

import java.io.File;

public class ScanResult
{
  final public Rom rom;
  final public RomFileEntry entry;
  
  ScanResult(Rom rom, RomFileEntry entry)
  {
    this.rom = rom;
    this.entry = entry;
  }
}
