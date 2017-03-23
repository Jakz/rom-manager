package jack.rm.files;

import com.github.jakz.romlib.data.game.Rom;
import com.pixbits.lib.io.archive.handles.Handle;

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
    int i = rom.game().compareTo(other.rom.game());
    return i == 0 ? path.path().compareTo(other.path.path()) : i;
  }
  
  public void assign()
  {
    rom.setHandle(path);
  }
}
