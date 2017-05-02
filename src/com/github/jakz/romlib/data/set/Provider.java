package com.github.jakz.romlib.data.set;

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
  private final String tag;

  private final String flavour;  
  private final String suffix;
  
  private final String author;
  
  private final Source source;
  
  public Provider(String name, String tag, String flavour, String suffix, String author, Source source)
  {
    this.name = name;
    this.tag = tag;
    this.flavour = flavour;
    this.suffix = suffix;
    this.author = author;
    this.type = Type.DAT_FILE;
    this.source = source;
  }

  public Provider(String name, String tag, Source source)
  {
    this(name, tag, "", "", "", source);
  }
  
  public Provider derive(String flavour, String suffix, String author, Source source)
  {
    return new Provider(name, tag, flavour, suffix, author, source);
  }
  
  public Type getType() { return type; }
  public String getName() { return name; }
  public String getTag() { return tag; }
  public String getFlavour() { return flavour; }
  public String getAuthor() { return author; }
  
  public boolean canBeUpdated() { return source != null; }
  public Source getSource() { return source; }
 
  private boolean hasSuffix() { return suffix != null && !suffix.isEmpty(); }
  private String builtSuffix() { return hasSuffix() ? ("-" + suffix) : ""; }
  
  public String getIdentifier() { return getTag()+builtSuffix(); }
  
  public String prettyName() {
    if (hasSuffix())
      return getName()+" ("+getFlavour()+")";
    else
      return getName();
  }
  
  public boolean equals(Object o)
  {
    return o instanceof Provider && ((Provider)o).getIdentifier().equals(getIdentifier());
  }
  
  public final static Provider DUMMY = new Provider("", "", null);
}
