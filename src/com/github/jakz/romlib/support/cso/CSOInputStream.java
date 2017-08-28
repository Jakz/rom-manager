package com.github.jakz.romlib.support.cso;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class CSOInputStream extends InputStream implements AutoCloseable
{
  private CSOInfo info;
  private RandomAccessFile in;
  private Path path;
  
  private int sector;
  private int offset;
  
  private long currentCache;
  private byte[] current;
    
  private byte[] buffer;
  
  private final Inflater inflater;
  
  public CSOInputStream()
  {
    sector = 0;
    offset = 0;
    
    currentCache = -1;
    current = new byte[2048];
    buffer = new byte[2048];
    
    inflater = new Inflater(true);
  }
  
  public CSOInputStream(final RandomAccessFile file, CSOInfo info)
  {
    this(info);
    this.in = file;
  }
  
  public CSOInputStream(final Path path, CSOInfo info)
  {
    this(info);
    this.path = path;
  }
  
  public CSOInputStream(CSOInfo info)
  {
    this();
    this.info = info;
    this.path = null;
    this.in = null;
  }
  
  public CSOInputStream(Path path) throws IOException
  {
    this();
    this.path = path;
    this.info = new CSOInfo(path);
  }
  
  private void cacheSector(int index) throws IOException
  {
    if (index >= info.offsets.length-1)
      return;
    
    if (info.compressedBitmap.get(index))
    {
      long offset = info.offsets[index];
      long length = info.offsets[index+1] - offset;
      
      in.seek(offset);
      in.readFully(buffer, 0, (int)length);
      
      inflater.reset();
      inflater.setInput(buffer, 0, (int)length);
      
      try
      {
        int i = inflater.inflate(current);
        
        if (i != 2048)
          throw new DataFormatException();
      } 
      catch (DataFormatException e)
      {
        throw new IOException("Sector "+sector+" of CSO file doesn't contain valid compressed data");
      }
    }
    else
    {
      long offset = info.offsets[index];
      in.seek(offset);
      in.read(current);
    }
   
    currentCache = index;
  }
  
  @Override
  public void reset()
  {
    sector = 0;
    offset = 0;
    currentCache = -1;
  }
  
  @Override
  public void close() throws IOException
  {
    if (in != null)
      in.close();
    System.out.println("closed");
  }
  
  @Override
  public int read() throws IOException
  {
    if (in == null)
      in = new RandomAccessFile(path.toFile(), "r");

    if (currentCache != sector)
      cacheSector(sector);

    if (sector >= info.sectorCount - 1)
      return -1;
    
    int value = current[offset] & 0xFF;
    
    ++offset;
    if (offset == info.blockSize)
    {
      ++sector;
      offset = 0;
    }
    
    return value;
  }
  
  @Override
  public int read(byte[] dest) throws IOException
  {
    return read(dest, 0, dest.length);
  }
  
  @Override
  public int read(byte[] dest, int s, int l) throws IOException
  {
    int i = 0;
    
    if (in == null)
      in = new RandomAccessFile(path.toFile(), "r");

    if (sector >= info.sectorCount - 1)
      return -1;
    
    while (i < l)
    {
      if (currentCache != sector)
        cacheSector(sector);

      /* if at any time we reached end of stream return current status */
      if (sector >= info.sectorCount - 1)
        return i > 0 ? i : -1;
      else
      {
        if (offset < info.blockSize)
        {
          int remainder = Math.min(l - i, info.blockSize - offset);
          System.arraycopy(current, offset, dest, s + i, remainder);
          offset += remainder;
          i += remainder;
        }
        
        if (offset == info.blockSize)
        {
          ++sector;
          offset = 0;
        }
      }
    }
    
    return i;
  }
}