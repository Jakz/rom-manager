package jack.rm.data.rom;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class RomGroup
{
  private String name;
  private final Set<Rom> roms;
  
  public RomGroup()
  {
    roms = new HashSet<>();
  }
  
  void setName(String name)
  {
    this.name = name;
  }
  
  void addRom(Rom rom)
  {
    roms.add(rom);
  }
  
  boolean contains(Rom rom)
  {
    return roms.contains(rom);
  }
  
  Iterator<Rom> iterator() { return roms.iterator(); }
}
