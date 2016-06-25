package jack.rm.data.rom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.*;

public abstract class RomPath
{
  public static enum Type
  {
    BIN(""),
    ZIP("zip")
    
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

  public static class Bin extends RomPath
  {
    public final Path file;

    public Bin(Path file)
    {
      super(Type.BIN);
      this.file = file.normalize();
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString(); }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
    @Override
    public String plainInternalName() { return plainName(); }
    
    @Override public long size() {
      try
      {
        return Files.size(file);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return 0;
      }
    }
    
    @Override public long uncompressedSize() { return size(); }
    
    @Override public boolean isArchive() { return false; }
    @Override public String getExtension() {
      String filename = file.getFileName().toString();
      int lastdot = filename.lastIndexOf('.');
      return lastdot != -1 ? filename.substring(lastdot+1) : "";
    }
    @Override public String getInternalExtension() { return getExtension(); }
    
    @Override
    public RomPath relocate(Path file)
    {
      return new Bin(file);
    }
    
    @Override
    public RomPath relocateInternal(String internalName)
    {
      throw new UnsupportedOperationException("a binary rompath doesn't have an internal filename");
    }
    
    @Override
    public InputStream getInputStream() throws IOException
    {
      return Files.newInputStream(file);
    }
  }
  
  public static class Archive extends RomPath
  {
    public static class Handle
    {
      private final Archive archive;
      private final Path internalPath;
      
      public Handle(Archive archive, Path internalPath)
      {
        this.archive = archive;
        this.internalPath = internalPath;
      }
      
      public void delete() throws IOException { Files.delete(internalPath); }
      
      public Path fileName() { return internalPath.getFileName(); }
      
      @Override public String toString() { return archive.file().toString()+":"+internalPath.toString(); }
    }
    
    
    public final Path file;
    public final String internalName;
    
    public Archive(Path file, String internalName)
    {
      super(Type.ZIP);
      this.file = file.normalize();
      this.internalName = internalName;
    }
    
    @Override public Path file() { return file; }
    @Override public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
    @Override public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
    @Override public String plainInternalName() { return internalName.substring(0, internalName.toString().lastIndexOf('.')); }
    @Override public String getInternalExtension() { return internalName.substring(internalName.toString().lastIndexOf('.')+1); }

    @Override public boolean isArchive() { return true; }
    
    @Override public String getExtension() { return type.ext; }
    
    @Override public long size()
    {
      try
      {
        return Files.size(file);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return 0;
      }
    }
    
    @Override public long uncompressedSize()
    {
      try (ZipFile zfile = new ZipFile(file.toFile()))
      {
        
        ZipEntry entry = zfile.getEntry(internalName);
        return entry.getSize();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return 0;
      }
    }
    
    private FileSystem openZipFS() throws URISyntaxException, IOException
    {
      URI ouri = file.toUri();
      URI uri = new URI("jar:file", ouri.getUserInfo(), ouri.getHost(), ouri.getPort(), ouri.getPath(), ouri.getQuery(), ouri.getFragment());
      return FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
    }
    
    public boolean renameInternalFile(String newName)
    {       
      try (FileSystem fs = openZipFS())
      {     
        Path sourcePath = fs.getPath(internalName);
        Path destPath = fs.getPath(newName); 
        
        Files.move(sourcePath, destPath);
        return true;
      }
      catch (URISyntaxException|IOException e)
      {
        e.printStackTrace();
        return false;
      }

      /*
      forEach(h -> {
        try {
        if (!h.fileName().toString().equals(newName))
          Files.delete(h.internalPath);
        }
        catch (Exception ee) { ee.printStackTrace(); }
      });
      
      */
    }
    
    @Override
    public RomPath relocate(Path file)
    {
      return new Archive(file, this.internalName);
    }
    
    @Override
    public RomPath relocateInternal(String internalName)
    {
      return new Archive(file, internalName);
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
    
    public void forEach(Consumer<Handle> lambda)
    {
      try (FileSystem fs = openZipFS())
      {    
        Files.walk(fs.getRootDirectories().iterator().next()).filter(p -> !Files.isDirectory(p)).map(p -> new Handle(this, p)).forEach(lambda);
      }
      catch (URISyntaxException|IOException e)
      {
        e.printStackTrace();
      }
    }
  }
}
