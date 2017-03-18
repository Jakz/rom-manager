package jack.rm.files;

import com.pixbits.lib.io.archive.handles.Handle;

import jack.rm.data.rom.Rom;

public class ScanResult implements Comparable<ScanResult>
{
  public Rom rom;
  public Handle path;
  
  public ScanResult(Rom rom, Handle path)
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
    return i == 0 ? path.path().compareTo(other.path.path()) : i;
  }
  
  public void assign()
  {
    rom.setHandle(path);
  }
}
