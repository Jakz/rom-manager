package jack.rm.files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pixbits.io.BinaryBuffer;

public class IPSPatch
{
  BinaryBuffer buffer;
  
  private abstract class Entry
  {
    int offset;
    int size;
    
    Entry(int offset, int size)
    {
      this.offset = offset;
      this.size = size;
    }
    
    public abstract int requiredLength();
  }
  
  private class NormalEntry extends Entry
  {
    byte[] data;
    
    NormalEntry(int offset, int size)
    {
      super(offset, size);
      this.data = new byte[size];
    }
    
    public int requiredLength() { return offset + size; }
  };
  
  private class RLEEntry extends Entry
  {
    byte value;
    int length;
    
    RLEEntry(int offset, int size, byte value, int length)
    {
      super(offset, size);
      this.value = value;
      this.length = length;
    }
    
    public int requiredLength() { return offset + length; }
  }
  
  Optional<Integer> trimSize;
  List<Entry> entries;
  int requiredLength;
  
  public IPSPatch(Path fileName) throws IOException, FileNotFoundException
  {
    buffer = new BinaryBuffer(fileName, BinaryBuffer.Mode.READ, ByteOrder.BIG_ENDIAN);
    entries = new ArrayList<>();
    
    String header = buffer.readString(5);
    
    if (!header.equals("PATCH"))
      throw new FileNotFoundException("Wrong header in IPS file: "+fileName);
    
    boolean finished = false;
    
    while (!finished)
    {
      int offset = buffer.readU24();
      
      
      if (offset == 0x454F46) // "EOF" in big endian bytes
        finished = true;
      else
      {
        int size = buffer.readU16();
        
        //System.out.printf("%#08x, %d\n", offset, size);
        
        Entry entry = null;
        
        if (size != 0)
        {
          entry = new NormalEntry(offset, size);
          buffer.read(((NormalEntry)entry).data);
        }
        else
        {     
          int length = buffer.readU16();
          entry = new RLEEntry(offset, size, buffer.readByte(), length);
        }
        
        entries.add(entry);
      }
    }
    
    if (!buffer.didReachEnd())
      trimSize = Optional.of(buffer.readU24());
    else
      trimSize = Optional.empty();
    
    buffer.close();
    
    Optional<Entry> farthestEntry = entries.stream().max( (r1, r2) -> r1.requiredLength() - r2.requiredLength());
    
    requiredLength = farthestEntry.get().requiredLength();
  }
  
  public void apply(BinaryBuffer buffer)
  {
    try
    {
      if (buffer.length() < requiredLength)
      {
        //System.out.println("Increasing IPS file from "+buffer.length()+" to "+requiredLength);
        buffer.resize(requiredLength);
      }
      
      for (Entry entry : entries)
      {
        if (entry instanceof NormalEntry)
        {
          buffer.replace(((NormalEntry)entry).data, entry.offset);
        }
        else if (entry instanceof RLEEntry)
        {
          byte value = ((RLEEntry)entry).value;
          for (int i = 0; i < ((RLEEntry)entry).length; ++i)
            buffer.write(value, entry.offset + i);
        }
      }

      if (trimSize.isPresent())
        buffer.resize(trimSize.get());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
