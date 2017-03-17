package jack.rm.files.romhandles2;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class RomHandle
{
  public static enum Type
  {
    BIN(""),
    ZIP("zip"),
    RAR("rar"),
    _7ZIP("7z")
    ;
    
    public final String ext;
    
    Type(String ext)
    {
      this.ext = ext;
    }
  }
  
  
  public final Type type;
  @Override
  public abstract String toString();
  public abstract Path file();
  public abstract String plainName();
  public abstract String plainInternalName();
  public abstract RomHandle relocate(Path file);
  public abstract RomHandle relocateInternal(String internalName);
  public abstract boolean isArchive();
  public abstract String getExtension();
  public abstract String getInternalExtension();
  public abstract InputStream getInputStream() throws IOException;
  public abstract long size();
  public abstract long uncompressedSize();
  
  RomHandle(Type type)
  {
    this.type = type;
  }
  
  public static RomHandle build(Type type, Path path, String... args)
  {
    switch (type)
    {
      case BIN: return new BinaryHandle(path);
      case ZIP: return new ZipHandle(path, args[0]);
      default: return null;
    }
  }
}
