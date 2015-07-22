package jack.rm.data.set;

public abstract class Provider
{
  public abstract String getTag();
  public abstract String getName();
  
  public boolean equals(Object o)
  {
    return o instanceof Provider && ((Provider)o).getTag().equals(getTag());
  }
}
