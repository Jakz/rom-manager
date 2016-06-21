package jack.rm.data.rom;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class RomGroup implements Iterable<Rom>
{
  private String name;
  private final Set<Rom> roms;
  private final int id;
  
  public RomGroup(int id)
  {
    this.id = id;
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
  
  boolean removeRom(Rom rom)
  {
    roms.remove(rom);
    return roms.isEmpty();
  }
  
  public boolean contains(Rom rom)
  {
    return roms.contains(rom);
  }
  
  public int getId() { return id; }
  
  public Iterator<Rom> iterator() { return roms.iterator(); }
}
