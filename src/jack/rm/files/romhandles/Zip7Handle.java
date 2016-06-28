package jack.rm.files.romhandles;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
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

import jack.rm.data.rom.Rom;
import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class Zip7Handle extends ArchiveHandle
{
  /*public static class Handle
  {
    private final Zip7Handle archive;
    private final Path internalPath;
    
    public Handle(Zip7Handle archive, Path internalPath)
    {
      this.archive = archive;
      this.internalPath = internalPath;
    }
    
    public void delete() throws IOException { Files.delete(internalPath); }
    
    public Path fileName() { return internalPath.getFileName(); }
    
    @Override public String toString() { return archive.file().toString()+":"+internalPath.toString(); }
  }*/
  
  
  public final int indexInArchive;
  public final String internalName;
  
  public Zip7Handle(Path file, String internalName, Integer indexInArchive)
  {
    super(Type._7ZIP, file.normalize());
    this.internalName = internalName;
    this.indexInArchive = indexInArchive;
  }
  
  protected IInArchive open()
  {
    try (RandomAccessFileInStream rfile = new RandomAccessFileInStream(new RandomAccessFile(file.toFile(), "r")))
    {
      return SevenZip.openInArchive(null, rfile);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
  
  @Override public Path file() { return file; }
  @Override public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
  @Override public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf('.')); }
  @Override public String plainInternalName() { return internalName.substring(0, internalName.toString().lastIndexOf('.')); }
  @Override public String getInternalExtension() { return internalName.substring(internalName.toString().lastIndexOf('.')+1); }

  @Override public String getExtension() { return type.ext; }
  
  @Override public long size()
  {
    try (IInArchive archive = open())
    {
      Long size = (Long)archive.getProperty(indexInArchive, PropID.PACKED_SIZE);
      return size != null ? size : 0;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return 0;
    }
  }
  
  @Override public long uncompressedSize()
  {
    try (IInArchive archive = open())
    {
      Long size = (Long)archive.getProperty(indexInArchive, PropID.SIZE);
      return size != null ? size : 0;
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return 0;
    }
  }

  public boolean renameInternalFile(String newName)
  {       
    return false;

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
    return new Zip7Handle(file, this.internalName, this.indexInArchive);
  }
  
  @Override
  public RomPath relocateInternal(String internalName)
  {
    return null;//new Zip7Handle(file, internalName);
  }
  
  @Override
  public InputStream getInputStream() throws IOException
  {
    IInArchive archive = open();
    return null;
  }
  
  private class ExtractStream implements ISequentialOutStream
  {
    @Override public int write(byte[] data)
    {
      return data.length;
    }
  }
  
  private class ExtractCallback implements IArchiveExtractCallback
  {
    private final IInArchive archive;
    private int index;
    
    ExtractCallback(IInArchive archive, int index)
    {
      this.archive = archive;
      this.index = index;
    }
    
    public ISequentialOutStream getStream(int index, ExtractAskMode mode)
    {
      this.index = index;
      if (mode != ExtractAskMode.EXTRACT) return null;
      
      return new ExtractStream();
      
    }
    
    public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException
    {
      
    }
    
    public void setOperationResult(ExtractOperationResult result) throws SevenZipException
    {
      
    }
    
    public void setCompleted(long completeValue) throws SevenZipException
    {
      
    }

    public void setTotal(long total) throws SevenZipException
    {
      
    }
  }
  
}