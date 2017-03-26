package jack.rm.files;

import com.github.jakz.romlib.data.game.Rom;
import com.pixbits.lib.io.archive.handles.Handle;

public class ScanResult implements Comparable<ScanResult>
{
  public final Rom rom;
  public final Handle handle;
  
  public ScanResult(Rom rom, Handle path)
  {
    this.rom = rom;
    this.handle = path;
  }
  
  @Override public boolean equals(Object other)
  {
    return other instanceof ScanResult && ((ScanResult)other).compareTo(this) == 0;
  }
  
  @Override public int compareTo(ScanResult other)
  {
    int i = rom.game().compareTo(other.rom.game());
    return i == 0 ? handle.path().compareTo(other.handle.path()) : i;
  }
  
  public void assign()
  {
    rom.setHandle(handle);
  }
}
