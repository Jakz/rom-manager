package com.github.jakz.romlib.data.game;

public interface Version 
{    
  public final static Version PROPER = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
  };
  
  public final static Version SAMPLE = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
  };
  
  public final static Version DEMO = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
  };

  public final static Version PROTO = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
  };
  
  public final static Version UNSPECIFIED = new Version() {
    @Override public boolean equals(Object obj) { return obj == this; }
  };
  
  public static class Numbered implements Version
  {
    private final int major;
    private final int minor;
    private final String suffix;
    
    public Numbered(int major, int minor, String suffix)
    {
      this.major = major;
      this.minor = minor;
      this.suffix = suffix;
    }
    
    public Numbered(int major, int minor)
    {
      this(major, minor, "");
    }
    
    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public String getSuffix() { return suffix; }
    public String toString() { return major + "." + minor + suffix; }
  }
  
  public final class Beta implements Version
  {
    private final String type;
    public Beta(String type)
    {
      this.type = type;
    }
    
    @Override public boolean equals(Object obj)
    {
      return obj instanceof Beta && ((Beta)obj).type.equals(type);
    }  
  }
  
  public final class Revision implements Version
  {
    private final String type;
    public Revision(String type)
    { 
      this.type = type;
    }
    
    @Override public boolean equals(Object obj)
    { 
      return (obj instanceof Revision) && ((Revision)obj).type.equals(type);
    }
  }
}
