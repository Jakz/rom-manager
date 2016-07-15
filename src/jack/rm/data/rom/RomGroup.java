package jack.rm.data.rom;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RomGroup implements Iterable<Rom>
{
  private String name;
  private final Set<Rom> roms;
  private final RomGroupID ident;
  
  public RomGroup(RomGroupID id)
  {
    this.ident = id;
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
  
  public int hashCode() { return ident.hashCode(); }
  public boolean equals(Object o) { return o instanceof RomGroup && ((RomGroup)o).ident.equals(ident); }
  
  public Iterator<Rom> iterator() { return roms.iterator(); }
}
