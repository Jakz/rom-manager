package com.github.jakz.romlib.support.patches;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.pixbits.lib.io.BinaryBuffer;

public class UPSPatch
{  
  long startSize;
  long endSize;
  
  long startCRC, endCRC, patchCRC;
  
  class PatchEntry
  {
    long skipAmount;
    byte[] data;
    
    PatchEntry(long skipAmount, int length)
    {
      this.skipAmount = skipAmount;
      this.data = new byte[length];
    }
  }
  
  List<PatchEntry> entries;
    
  public UPSPatch(Path filename) throws FileNotFoundException, IOException
  {
    entries = new ArrayList<>();
    
    try (BinaryBuffer buffer = new BinaryBuffer(filename, BinaryBuffer.Mode.READ, ByteOrder.LITTLE_ENDIAN))
    {

      String header = buffer.readString(4);
      
      if (!header.equals("UPS1"))
        throw new FileNotFoundException("Wrong header in UPS file: "+filename);
  
      startSize = readVLint(buffer);
      endSize = readVLint(buffer);
      
      int startPatchPosition = buffer.position();
      
      buffer.position((int)buffer.length()-12);
      
      startCRC = buffer.readU32();
      endCRC = buffer.readU32();
      patchCRC = buffer.readU32();
      
      buffer.position(startPatchPosition);
  
      System.out.println("PATCH CRC: "+Long.toHexString(patchCRC));
      
      while (buffer.position() < buffer.length()-12)
      {
        long skipAmount = readVLint(buffer);
        int startPosition = buffer.position();
        int endPosition = startPosition;
        while (buffer.readByte() != 0x00)
          ++endPosition;
        
        PatchEntry entry = new PatchEntry(skipAmount, endPosition - startPosition);
        buffer.read(entry.data, startPosition);
        entries.add(entry);
      }
    
    }
  }
  
  private long readVLint(BinaryBuffer buffer)
  {
    long result = 0, shift = 0;

    while (true)
    {
      byte octet = buffer.readByte();

      
      if ((octet & 0x80) != 0)
      {
        result += (octet & 0x7F) << shift;
        break;
      }
      
      result += (octet | 0x80) << shift;
      shift += 7; 
    }
    
    return result;
  }
  
  public void apply(BinaryBuffer buffer)
  {
    for (PatchEntry entry : entries)
    {
      buffer.advance((int)entry.skipAmount);
      byte[] data = new byte[entry.data.length];
      
      int replacePosition = buffer.position();
      buffer.read(data);
      
      for (int i = 0; i < data.length; ++i)
      {
        data[i] = (byte)(data[i] ^ entry.data[i]);
      }
      
      buffer.position(replacePosition);
      buffer.write(data);
    }
  }
}
