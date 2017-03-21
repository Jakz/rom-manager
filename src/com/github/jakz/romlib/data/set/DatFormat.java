package com.github.jakz.romlib.data.set;

public final class DatFormat
{
  private final String ident;
  private final String extension;
  
  public DatFormat(String ident, String extension)
  {
    this.ident = ident;
    this.extension = extension;
  }
  
  public boolean is(String ident) { return ident.equals(ident); }
  
  public String getIdent() { return ident; }
  public String getExtension() { return extension; }
  
  public static final DatFormat DUMMY = new DatFormat("dummy", "dat");
}
