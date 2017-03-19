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
  
  public String getIdent() { return ident; }
  public String getExtension() { return extension; }
}
