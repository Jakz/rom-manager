package com.pixbits.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.pixbits.algorithm.BoyerMooreByte;

public class BinaryBuffer implements AutoCloseable
{
  private final ByteBuffer buffer;
  private ByteOrder order;
  private int position;
  
  public BinaryBuffer(String fileName, Mode mode, ByteOrder order) throws FileNotFoundException, IOException
  {
    this(Paths.get(fileName), mode, order);
  }
  
  public BinaryBuffer(Path fileName, Mode mode, ByteOrder order) throws FileNotFoundException, IOException
  {
    position = 0;
    
    String smode = null;
    MapMode mapMode = null;
    
    
    switch (mode)
    {
      case READ:
      {
        smode = "r";
        mapMode = MapMode.READ_ONLY;
        break;
      }
      case WRITE:
      {
        smode = "rw"; 
        mapMode = MapMode.READ_WRITE;
        break;
      }
    }
    
    try (RandomAccessFile file = new RandomAccessFile(fileName.toFile(), smode))
    {
      buffer = file.getChannel().map(mapMode, 0, file.length());
    }
    
    this.order = order;
  }
  
  void setByteOrder(ByteOrder order)
  {
    this.order = order;
  }
  
  private void reverse(byte[] data)
  {
    for (int i = 0; i < data.length/2; ++i)
    {
      byte tmp = data[i];
      data[i] = data[data.length - i - 1];
      data[data.length - i - 1] = tmp;
    }
  }
  
  private void readOrdered(byte[] data)
  {
    buffer.get(data);
    if (order == ByteOrder.BIG_ENDIAN)
      reverse(data);
  }
  
  public boolean didReachEnd()
  {
    return buffer.remaining() == 0;
  }
  
  public void skip(int length)
  {
    buffer.position(buffer.position()+length);
  }
  
  public void read(byte[] bytes)
  {
    readOrdered(bytes);
  }
  
  public byte readByte()
  {
    return buffer.get();
  }
  
  public int readU8()
  {
    return buffer.get() & 0xFF;
  }

  public int readU24()
  {
    byte[] data = new byte[3];
    readOrdered(data);
    return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8) | ((data[2] & 0xFF) << 16);
  }
  
  public int readU16()
  {
    byte[] data = new byte[2];
    readOrdered(data);
    return (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
  }
    
  public String readString(int length)
  {
    byte[] bbuffer = new byte[length];
    buffer.get(bbuffer);

    return new String(bbuffer);
  }
  
  private Optional<BufferPosition> scanForDataWithCRC(byte[] data)
  {
    Checksum crc = new Adler32();
    crc.update(data, 0, data.length);
    
    final long destCRC = crc.getValue();
    final byte[] tempBuffer = new byte[data.length];
    
    for (int i = 0; i < buffer.limit() - data.length; ++i)
    {
      if (buffer.get(i) == data[0])
      {
        buffer.position(i);
        buffer.get(tempBuffer);
        
        crc.reset();
        crc.update(tempBuffer, 0, tempBuffer.length);
        
        if (crc.getValue() == destCRC && Arrays.equals(data, tempBuffer))
          return Optional.of(new BufferPosition(i));
            
      }
    }
    
    return Optional.empty();
  }
  
  private Optional<BufferPosition> scanForDataBayerMoore(byte[] data)
  {
    BoyerMooreByte boyerMore = new BoyerMooreByte(data); 
    
    int index = boyerMore.search(buffer);
    
    return index != -1 ? Optional.of(new BufferPosition(index)) : Optional.empty();
  }
  
  public Optional<BufferPosition> scanForData(byte[] data)
  {
    return scanForDataBayerMoore(data);
  }
  
  public void close()
  {
    if (buffer.isDirect())
    {
      try 
      {
        Method cleaner = buffer.getClass().getMethod("cleaner");
        cleaner.setAccessible(true);
        Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
        clean.setAccessible(true);
        clean.invoke(cleaner.invoke(buffer));
      } 
      catch(Exception ex)
      { 
        ex.printStackTrace();
      }
    }
  }
  
  public static enum Mode
  {
    READ,
    WRITE
  };
}
