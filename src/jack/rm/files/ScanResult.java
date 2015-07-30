package jack.rm.files;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomPath;

public class ScanResult implements Comparable<ScanResult>
{
  public Rom rom;
  public RomPath path;
  
  public ScanResult(Rom rom, RomPath path)
  {
    this.rom = rom;
    this.path = path;
  }
  
  @Override public boolean equals(Object other)
  {
    return other instanceof ScanResult && ((ScanResult)other).compareTo(this) == 0;
  }
  
  @Override public int compareTo(ScanResult other)
  {
    int i = rom.compareTo(other.rom);
    return i == 0 ? path.file().compareTo(other.path.file()) : i;
  }
  
  public void assign()
  {
    rom.setPath(path);
  }
}
