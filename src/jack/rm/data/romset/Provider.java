package jack.rm.data.romset;

import java.net.MalformedURLException;
import java.net.URL;

public final class Provider
{
  public static enum Type
  {
    DAT_FILE("Dat File"),
    SCRAPER("Scraper")
    ;
    
    public final String caption;
    
    Type(String caption) { this.caption = caption; }
  };
  
  private final Type type;
  
  private final String name;
  private final String flavour;
  
  private final String tag;
  private final String suffix;
  
  private final String description;
  
  private URL url;
  
  public Provider(String name, String tag, String flavour, String suffix, String description, String url)
  {
    this.name = name;
    this.tag = tag;
    this.flavour = flavour;
    this.suffix = suffix;
    this.description = description;
    this.type = Type.DAT_FILE;
    
    try {this.url = new URL(url);}
    catch (MalformedURLException e)
    {
      e.printStackTrace(); 
    }
  }
  
  public Provider(String name, String tag, String url)
  {
    this(name, tag, null, null, null, url);
  }
  
  public Provider derive(String flavour, String suffix, String description, String url)
  {
    return new Provider(name, tag, flavour, suffix, description, url);
  }
  
  public Type getType() { return type; }
  public String getTag() { return tag; }
  public String getName() { return name; }
  public String getSuffix() { return suffix; }
  public String getFlavour() { return flavour; }
  public URL getURL() { return url; }
 
  public boolean hasSuffix() { return getSuffix() != null; }
  public String builtSuffix() { return hasSuffix() ? "-" + getSuffix() : ""; }
  
  public String prettyName() {
    if (getSuffix() != null)
      return getName()+" ("+getFlavour()+")";
    else
      return getName();
  }
  
  public boolean equals(Object o)
  {
    return o instanceof Provider && ((Provider)o).getTag().equals(getTag()) && ((Provider)o).getSuffix().equals(getSuffix());
  }
}
