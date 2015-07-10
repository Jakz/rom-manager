package jack.rm.data;

import java.io.File;

public class ScanResult
{
  public Rom rom;
  public RomFileEntry entry;
  
  ScanResult(Rom rom, RomFileEntry entry)
  {
    this.rom = rom;
    this.entry = entry;
  }
}
