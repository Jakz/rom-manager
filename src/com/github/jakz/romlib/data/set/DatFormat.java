package com.github.jakz.romlib.data.set;

public final class DatFormat
{
  private final String longIdentifier;
  private final String ident;
  private final String extension;
  
  private DatFormat(String longIdentifier, String ident, String extension)
  {
    this.longIdentifier = longIdentifier;
    this.ident = ident;
    this.extension = extension;
  }
  
  public boolean is(String ident) { return ident.equals(ident); }
  
  public String getLongIdentifier() { return longIdentifier; }
  public String getIdent() { return ident; }
  public String getExtension() { return extension; }
  
  public static DatFormat of(String longIdentifier, String ident, String extension)
  { 
    return new DatFormat(longIdentifier, ident, extension);
  }
  
  public static final DatFormat DUMMY = new DatFormat("dummmy", "dummy", "dat");
}
