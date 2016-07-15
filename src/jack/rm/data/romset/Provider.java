package jack.rm.data.romset;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class Provider
{
  public static final class Source
  {
    private URL url;
    private final Map<String, String> postArguments;
    
    public Source(String url, String... args)
    {
      try { this.url = new URL(url); } catch (MalformedURLException e) { e.printStackTrace(); }
      
      if (args != null)
      {
        if (args.length % 2 != 0)
          throw new IllegalArgumentException("post arguments for Provider.Source must be in pairs of key,value");
        
        postArguments = new HashMap<String,String>();
        
        for (int i = 0; i < args.length/2; ++i)
          postArguments.put(args[i*2], args[i*2 + 1]);
      }
      else
        postArguments = null;
    }
    
    public URL getURL() { return url; }
    public Map<String,String> getPostArguments() { return postArguments; }
  }
  
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
  
  private final String author;
  private final String description;
  
  private Source source;
  
  public Provider(String name, String tag, String flavour, String suffix, String description, String author, Source source)
  {
    this.name = name;
    this.tag = tag;
    this.flavour = flavour;
    this.suffix = suffix;
    this.description = description;
    this.type = Type.DAT_FILE;
    this.author = author;
    this.source = source;
  }
  
  public Provider(String name, String tag, Source source)
  {
    this(name, tag, null, null, null, null, source);
  }
  
  public Provider derive(String flavour, String suffix, String description, String author, Source source)
  {
    return new Provider(name, tag, flavour, suffix, description, author, source);
  }
  
  public Type getType() { return type; }
  public String getTag() { return tag; }
  public String getName() { return name; }
  public String getSuffix() { return suffix; }
  public String getFlavour() { return flavour; }
  public String getAuthor() { return author; }
  
  public boolean canBeUpdated() { return source != null; }
  public Source getSource() { return source; }
 
  public boolean hasSuffix() { return getSuffix() != null && !getSuffix().isEmpty(); }
  public String builtSuffix() { return hasSuffix() ? "-" + getSuffix() : ""; }
  
  public String prettyName() {
    if (hasSuffix())
      return getName()+" ("+getFlavour()+")";
    else
      return getName();
  }
  
  public boolean equals(Object o)
  {
    return o instanceof Provider && ((Provider)o).getTag().equals(getTag()) && ((Provider)o).getSuffix().equals(getSuffix());
  }
}
