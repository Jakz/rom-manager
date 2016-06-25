package jack.rm.data.romset;

public final class Provider
{
  private final String name;
  private final String flavour;
  
  private final String tag;
  private final String suffix;
  
  public Provider(String name, String tag, String flavour, String suffix)
  {
    this.name = name;
    this.tag = tag;
    this.flavour = flavour;
    this.suffix = suffix;
  }
  
  public Provider(String name, String tag)
  {
    this(name, tag, null, null);
  }
  
  public Provider derive(String flavour, String suffix)
  {
    return new Provider(name, tag, flavour, suffix);
  }
  
  public String getTag() { return tag; }
  public String getName() { return name; }
  public String getSuffix() { return suffix; }
  public String getFlavour() { return flavour; }
 
  public boolean hasSuffix() { return getSuffix() != null; }
  public String builtSuffix() { return hasSuffix() ? "-" + getSuffix() : ""; }
  
  public boolean equals(Object o)
  {
    return o instanceof Provider && ((Provider)o).getTag().equals(getTag()) && ((Provider)o).getSuffix().equals(getSuffix());
  }
}
