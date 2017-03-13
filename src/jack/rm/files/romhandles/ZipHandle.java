package jack.rm.files.romhandles;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ZipHandle extends ArchiveHandle
{
  public static class Handle
  {
    private final ZipHandle archive;
    private final Path internalPath;
    
    public Handle(ZipHandle archive, Path internalPath)
    {
      this.archive = archive;
      this.internalPath = internalPath;
    }
    
    public void delete() throws IOException { Files.delete(internalPath); }
    
    public Path fileName() { return internalPath.getFileName(); }
    
    @Override public String toString() { return archive.file().toString()+":"+internalPath.toString(); }
  }
  
  
  public final String internalName;
  
  public ZipHandle(Path file, String internalName)
  {
    super(Type.ZIP, file.normalize());
    this.internalName = internalName;
  }
  
  @Override public Path file() { return file; }
  @Override public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
  @Override public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
  @Override public String plainInternalName() { return internalName.substring(0, internalName.toString().lastIndexOf('.')); }
  @Override public String getInternalExtension() { return internalName.substring(internalName.toString().lastIndexOf('.')+1); }

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
  public RomHandle relocate(Path file)
  {
    return new ZipHandle(file, this.internalName);
  }
  
  @Override
  public RomHandle relocateInternal(String internalName)
  {
    return new ZipHandle(file, internalName);
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
    
    RomZipInputStream(ZipHandle archive) throws IOException
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
  
  public void forEach(Consumer<ZipHandle.Handle> lambda)
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