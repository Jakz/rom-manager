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
  
  public final static Version BETA = new Version() {
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
    int major;
    int minor;
    
    public Numbered(int major, int minor)
    {
      this.major = major;
      this.minor = minor;
    }
    
    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public String toString() { return major != 0 || minor != 0 ? (major + "." + minor) : ""; }
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
