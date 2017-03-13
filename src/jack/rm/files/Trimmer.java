package jack.rm.files;

import java.io.IOException;

import com.pixbits.lib.io.BinaryBuffer;

public class Trimmer
{
  public static long trimOverdump(BinaryBuffer buffer, long destSize) throws IOException
  {    
    long size = buffer.length();
    
    if (destSize < size)
      buffer.resize(destSize);
    return size - destSize;
  }
  
  public static long trim(BinaryBuffer buffer, byte[] filler) throws IOException
  {
    long size = buffer.length();
    long startSize = size;
    
    byte current = buffer.read(size-1);
    
    while (contains(filler, current))
    {
      --size;
      current = buffer.read(size-1);
    }
    
    buffer.resize(size);
    return startSize - size;
  }
  
  private static boolean contains(byte[] array, byte which)
  {
    for (byte b : array)
      if (b == which)
        return true;
    
    return false;
  }
}
