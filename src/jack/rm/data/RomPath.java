package jack.rm.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.zip.*;

public abstract class RomPath
{
  public final RomType type;
  @Override
  public abstract String toString();
  public abstract Path file();
  public abstract String plainName();
  public abstract RomPath build(Path file);
  public abstract boolean isArchive();
  public abstract String getExtension();
  public abstract InputStream getInputStream() throws IOException;
  
  RomPath(RomType type)
  {
    this.type = type;
  }

  public static class Bin extends RomPath
  {
    public final Path file;

    public Bin(Path file)
    {
      super(RomType.BIN);
      this.file = file.normalize();
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString(); }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
    
    @Override public boolean isArchive() { return false; }
    @Override public String getExtension() {
      String filename = file.getFileName().toString();
      int lastdot = filename.lastIndexOf('.');
      return lastdot != -1 ? filename.substring(lastdot+1) : "";
    }
    
    @Override
    public RomPath build(Path file)
    {
      return new Bin(file);
    }
    
    @Override
    public InputStream getInputStream() throws IOException
    {
      return Files.newInputStream(file);
    }
  }
  
  public static class Archive extends RomPath
  {
    public final Path file;
    public final String internalName;
    
    public Archive(Path file, String internalName)
    {
      super(RomType.ZIP);
      this.file = file.normalize();
      this.internalName = internalName;
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
    
    @Override public boolean isArchive() { return true; }
    
    @Override public String getExtension() { return type.ext; }
    
    @Override
    public RomPath build(Path file)
    {
      return new Archive(file, this.internalName);
    }
    
    @Override
    public InputStream getInputStream() throws IOException
    {
      return new RomZipInputStream(this);
    }
    
    private class RomZipInputStream extends InputStream
    {
      private final ZipFile file;
      private final ZipEntry entry;
      private final InputStream is;
      
      RomZipInputStream(Archive archive) throws IOException
      {
        this.file = new ZipFile(archive.file.toFile());
        this.entry = file.getEntry(archive.internalName);
        this.is = file.getInputStream(entry);
      }
      
      @Override public int read() throws IOException
      {
        return is.read();
      }
      
      @Override public void close() throws IOException
      {
        is.close();
        file.close();
      }
    }
  }
}
