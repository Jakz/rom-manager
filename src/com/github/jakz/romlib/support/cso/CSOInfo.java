package com.github.jakz.romlib.support.cso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.BitSet;

public class CSOInfo
{
  private int headerSize;
  private long uncompressedSize;
  int blockSize;
  private int version;
  private int indexShift;
  
  int sectorCount;
  BitSet compressedBitmap;
  long[] offsets;
  
  public long uncompressedSize() { return uncompressedSize; }
  public int sectorCount() { return sectorCount; }
   
  public CSOInfo(RandomAccessFile in) throws IOException
  {
    loadHeader(in);
  }
  
  public CSOInfo(Path path) throws IOException
  {
    try (RandomAccessFile in = new RandomAccessFile(path.toFile(), "r"))
    {
      loadHeader(in);
    }
  }
  
  public void cacheOffsets(RandomAccessFile in) throws IOException
  {
    in.seek(4+4+8+4+1+1+2);

    compressedBitmap = new BitSet(sectorCount);
    
    ByteBuffer offsets = ByteBuffer.allocate(4 * sectorCount);
    offsets.order(ByteOrder.LITTLE_ENDIAN);
    in.readFully(offsets.array());
    
    this.offsets = new long[sectorCount];
    
    /* each offset is a uint32_t, lower 31 bits are the position of the sector in the file
     * shifted by indexShift from header, while 32th bit is 1 if the sector is uncompressed
     */
    
    for (int i = 0; i < sectorCount; ++i)
    {
      int offset = offsets.getInt();
      if ((offset & 0x80000000) == 0)
        compressedBitmap.set(i);
      this.offsets[i] = (offset & 0x7FFFFFFFL);
    }

    for (int i = 0; i < sectorCount; ++i)
    {
      long offset = this.offsets[i];
      long realOffset = (offset & ~0x80000000L) << indexShift;
      this.offsets[i] = realOffset;
    }
  }
  
  private void loadHeader(RandomAccessFile in) throws IOException
  {    
    in.seek(0);
    
    ByteBuffer header = ByteBuffer.allocate(4+4+8+4+1+1+2);
    header.order(ByteOrder.LITTLE_ENDIAN);
    in.readFully(header.array());
    
    byte[] magic = new byte[4];
    header.get(magic);
    
    if (!Arrays.equals(magic, "CISO".getBytes()))
      throw new IllegalArgumentException("CSO Image should start with CISO magic value.");;
    
    headerSize = header.getInt();
    uncompressedSize = header.getLong();
    blockSize = header.getInt();
    version = header.get();
    indexShift = header.get();
    
    sectorCount = (int)Math.ceil((float)uncompressedSize / blockSize) + 1;
  }
}