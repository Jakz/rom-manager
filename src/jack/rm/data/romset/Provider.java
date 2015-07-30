package jack.rm.data.romset;

public abstract class Provider
{
  public abstract String getTag();
  public abstract String getName();
  
  public boolean equals(Object o)
  {
    return o instanceof Provider && ((Provider)o).getTag().equals(getTag());
  }
}
