package jack.rm.data;

public class ScanResult implements Comparable<ScanResult>
{
  public Rom rom;
  public RomPath entry;
  
  public ScanResult(Rom rom, RomPath entry)
  {
    this.rom = rom;
    this.entry = entry;
  }
  
  @Override public boolean equals(Object other)
  {
    return other instanceof ScanResult && ((ScanResult)other).compareTo(this) == 0;
  }
  
  @Override public int compareTo(ScanResult other)
  {
    int i = rom.compareTo(other.rom);
    return i == 0 ? entry.file().compareTo(other.entry.file()) : i;
  }
}
