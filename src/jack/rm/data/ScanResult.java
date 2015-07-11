package jack.rm.data;

public class ScanResult implements Comparable<ScanResult>
{
  public Rom rom;
  public RomFileEntry entry;
  
  ScanResult(Rom rom, RomFileEntry entry)
  {
    this.rom = rom;
    this.entry = entry;
  }
  
  public int compareTo(ScanResult other)
  {
    int i = rom.compareTo(rom);
    return i != 0 ? entry.file().compareTo(other.entry.file()) : i;
  }
}
