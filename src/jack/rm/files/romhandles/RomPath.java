package jack.rm.files.romhandles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.zip.*;

public abstract class RomPath
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
  public abstract RomPath relocate(Path file);
  public abstract RomPath relocateInternal(String internalName);
  public abstract boolean isArchive();
  public abstract String getExtension();
  public abstract String getInternalExtension();
  public abstract InputStream getInputStream() throws IOException;
  public abstract long size();
  public abstract long uncompressedSize();
  
  RomPath(Type type)
  {
    this.type = type;
  }
  
  public static RomPath build(Type type, Path path, String... args)
  {
    switch (type)
    {
      case BIN: return new BinaryHandle(path);
      case ZIP: return new ZipHandle(path, args[0]);
      default: return null;
    }
  }
}
