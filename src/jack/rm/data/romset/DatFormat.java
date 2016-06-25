package jack.rm.data.romset;

public final class DatFormat
{
  private final String ident, extension;
  
  public DatFormat(String ident, String extension)
  {
    this.ident = ident;
    this.extension = extension;
  }
  
  public String getIdent() { return ident; }
  public String getExtension() { return extension; }
}
