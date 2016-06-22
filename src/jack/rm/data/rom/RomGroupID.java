package jack.rm.data.rom;

public class RomGroupID 
{
  private int ident;
  
  public RomGroupID(int ident)
  {
    this.ident = ident;
  }
  
  public boolean equals(Object o)
  {
    return o instanceof RomGroupID && ((RomGroupID)o).ident == ident; 
  }
  
  public int hashCode()
  {
    return ident;
  }
}
