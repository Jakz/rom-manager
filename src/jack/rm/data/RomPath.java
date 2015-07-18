package jack.rm.data;

import java.nio.file.*;

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
      this.file = file;
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString(); }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().length()-4); }
    
    @Override public boolean isArchive() { return false; }
    @Override public String getExtension() {
      String filename = file.getFileName().toString();
      int lastdot = filename.lastIndexOf('.');
      return lastdot != -1 ? filename.substring(lastdot) : "";
    }
    
    @Override
    public RomPath build(Path file)
    {
      return new Bin(file);
    }
  }
  
  public static class Archive extends RomPath
  {
    public final Path file;
    public final String internalName;
    
    public Archive(Path file, String internalName)
    {
      super(RomType.ZIP);
      this.file = file;
      this.internalName = internalName;
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().length()-4); }
    
    @Override public boolean isArchive() { return true; }
    
    @Override public String getExtension() { return type.ext; }
    
    @Override
    public RomPath build(Path file)
    {
      return new Archive(file, this.internalName);
    }
  }
}
