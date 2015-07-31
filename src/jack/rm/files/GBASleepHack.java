package jack.rm.files;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pixbits.io.*;

public class GBASleepHack
{
  private static final int[] regData = new int[16];
  private static final int[] regAddr = new int[16];
  private static final int[] regLastWrite = new int[16];
  
  private static final int LDR_MASK = 0xFE7F0000;
  private static final int LDR_BITS = 0xE41F0000;
  private static final int STR_MASK = 0xFE700FFF;
  private static final int STR_BITS = 0xE4000000;
  private static final int THUMB_LDR_MASK = 0xF800;
  private static final int THUMB_LDR_BITS = 0x4800;
  private static final int THUMB_STR_MASK = 0xFFC0;
  private static final int THUMB_STR_BITS = 0x6000;
  
  private static final int UP_DOWN_BIT_MASK = 0x800000;
  
  private static final int REG_DEST_SHIFT = 12;
  private static final int REG_SRC_SHIFT = 16;
  private static final int REG_MASK = 0x0F;
  private static final int OFFSET_MASK = 0xFFF;
  
  private static final int THUMB_LDR_RD_SHIFT = 8;
  private static final int THUMB_STR_RB_SHIFT = 3;
  private static final int THUMB_STR_RD_SHIFT = 0;
  private static final int THUMB_REG_MASK = 0x07;
  private static final int THUMB_OFFSET_MASK = 0xFF;
  
  byte[] patch = new byte[] {0x01, 0x13, (byte)0xA0, (byte)0xE3, 0x4C, 0x00, 0x01, (byte)0xE5, 0x34, 0x00, (byte)0x8F, (byte)0xE2, 0x48, 0x00, 0x01, (byte)0xE5, (byte)0xE0, 0x01, (byte)0x9F, (byte)0xE5, 0x60, 0x00, 0x01, (byte)0xE5, (byte)0xDC, 0x01, 
      (byte)0x9F, (byte)0xE5, 0x5C, 0x00, 0x01, (byte)0xE5, (byte)0xD8, 0x01, (byte)0x9F, (byte)0xE5, 0x58, 0x00, 0x01, (byte)0xE5, (byte)0xD4, 0x01, (byte)0x9F, (byte)0xE5, 0x54, 0x00, 0x01, (byte)0xE5, (byte)0xD0, 0x01, (byte)0x9F, (byte)0xE5, 
      0x50, 0x00, 0x01, (byte)0xE5, (byte)0xCC, 0x01, (byte)0x9F, (byte)0xE5, 0x04, 0x00, 0x01, (byte)0xE5, 0x1E, (byte)0xFF, 0x2F, (byte)0xE1, 0x30, 0x21, (byte)0x90, (byte)0xE5, (byte)0xFF, 0x24, (byte)0xC2, (byte)0xE3, (byte)0xFF, 0x28, 
      (byte)0xC2, (byte)0xE3, (byte)0xF7, 0x00, 0x52, (byte)0xE3, 0x2E, 0x00, 0x00, 0x0A, (byte)0xF3, 0x00, 0x52, (byte)0xE3, 0x13, 0x00, 0x00, 0x0A, 0x01, 0x01, 0x12, (byte)0xE3, 0x06, 0x00, 0x00, 0x1A, 
      0x43, 0x34, (byte)0xA0, (byte)0xE3, 0x03, 0x37, (byte)0x83, (byte)0xE3, 0x30, 0x31, (byte)0x80, (byte)0xE5, 0x01, 0x1A, (byte)0x81, (byte)0xE3, 0x02, 0x2C, (byte)0x80, (byte)0xE2, (byte)0xB0, 0x10, (byte)0xC2, (byte)0xE1, 0x4C, (byte)0xF0, 
      0x10, (byte)0xE5, (byte)0xFF, 0x3C, (byte)0xC2, (byte)0xE3, (byte)0xFF, 0x30, (byte)0xC3, (byte)0xE3, 0x7C, (byte)0xC1, (byte)0x9F, (byte)0xE5, 0x0C, 0x00, 0x53, (byte)0xE1, 0x4C, (byte)0xF0, 0x10, 0x15, 0x01, 0x02, 0x11, (byte)0xE3, 
      (byte)0xF4, (byte)0xFF, (byte)0xFF, 0x0A, 0x01, 0x3A, (byte)0xA0, (byte)0xE3, 0x02, 0x2C, (byte)0x80, (byte)0xE2, (byte)0xB2, 0x30, (byte)0xC2, (byte)0xE1, (byte)0xF0, (byte)0xFF, (byte)0xFF, (byte)0xEA, 0x20, 0x10, (byte)0x8F, (byte)0xE2, 0x58, 0x30, 
      (byte)0x8F, (byte)0xE2, 0x02, 0x24, (byte)0xA0, (byte)0xE3, 0x04, 0x00, (byte)0x91, (byte)0xE4, 0x04, 0x00, (byte)0x82, (byte)0xE4, 0x03, 0x00, 0x51, (byte)0xE1, (byte)0xFB, (byte)0xFF, (byte)0xFF, (byte)0xBA, 0x02, 0x04, (byte)0xA0, (byte)0xE3, 
      0x01, 0x00, (byte)0x80, (byte)0xE2, 0x10, (byte)0xFF, 0x2F, (byte)0xE1, 0x20, 0x20, (byte)0x83, 0x05, 0x00, 0x03, 0x1C, 0x18, 0x25, 0x18, 0x01, 0x02, 0x5A, 0x18, 0x19, 0x09, 0x56, 0x1A, 
      0x09, 0x09, 0x76, 0x18, 0x12, 0x1A, 0x17, 0x1A, (byte)0xD2, 0x20, 0x00, 0x02, 0x15, 0x21, 0x09, 0x02, 0x10, (byte)0x80, 0x19, (byte)0x80, 0x20, (byte)0x80, 0x29, (byte)0x80, 0x18, 0x0B, 
      0x30, (byte)0x80, 0x39, (byte)0x80, (byte)0xC1, 0x02, 0x08, 0x39, (byte)0xFC, 0x20, 0x08, 0x60, 0x01, (byte)0xDF, 0x00, (byte)0xDF, (byte)0xF0, 0x4F, 0x2D, (byte)0xE9, 0x60, 0x10, (byte)0x80, (byte)0xE2, (byte)0xFC, 0x03, 
      (byte)0xB1, (byte)0xE8, (byte)0xFC, 0x03, 0x2D, (byte)0xE9, (byte)0xFC, 0x03, (byte)0xB1, (byte)0xE8, (byte)0xFC, 0x03, 0x2D, (byte)0xE9, 0x02, 0x1C, (byte)0x80, (byte)0xE2, (byte)0xB0, 0x40, (byte)0xD1, (byte)0xE1, 0x30, 0x51, (byte)0x90, (byte)0xE5, 
      (byte)0xB0, 0x60, (byte)0xD0, (byte)0xE1, (byte)0xD0, 0x10, (byte)0x9F, (byte)0xE5, 0x00, 0x12, (byte)0x80, (byte)0xE5, 0x03, 0x11, (byte)0xA0, (byte)0xE3, 0x03, 0x17, (byte)0x81, (byte)0xE3, 0x30, 0x11, (byte)0x80, (byte)0xE5, (byte)0xB4, 0x08, 
      (byte)0xC0, (byte)0xE1, (byte)0x80, 0x10, (byte)0x86, (byte)0xE3, (byte)0xB0, 0x10, (byte)0xC0, (byte)0xE1, 0x00, 0x00, 0x03, (byte)0xEF, 0x01, 0x03, (byte)0xA0, (byte)0xE3, 0x30, 0x11, (byte)0x90, (byte)0xE5, 0x0C, 0x10, 0x01, (byte)0xE2, 
      0x0C, 0x00, 0x51, (byte)0xE3, (byte)0xFA, (byte)0xFF, (byte)0xFF, 0x1A, (byte)0xB6, 0x10, (byte)0xD0, (byte)0xE1, (byte)0x9F, 0x00, 0x51, (byte)0xE3, (byte)0xFC, (byte)0xFF, (byte)0xFF, 0x1A, (byte)0xB6, 0x10, (byte)0xD0, (byte)0xE1, (byte)0xA0, 0x00, 
      0x51, (byte)0xE3, (byte)0xFC, (byte)0xFF, (byte)0xFF, 0x1A, (byte)0xB6, 0x10, (byte)0xD0, (byte)0xE1, (byte)0x9F, 0x00, 0x51, (byte)0xE3, (byte)0xFC, (byte)0xFF, (byte)0xFF, 0x1A, (byte)0xB6, 0x10, (byte)0xD0, (byte)0xE1, (byte)0xA0, 0x00, 0x51, (byte)0xE3, 
      (byte)0xFC, (byte)0xFF, (byte)0xFF, 0x1A, (byte)0xB6, 0x10, (byte)0xD0, (byte)0xE1, (byte)0x9F, 0x00, 0x51, (byte)0xE3, (byte)0xFC, (byte)0xFF, (byte)0xFF, 0x1A, 0x02, 0x1C, (byte)0x80, (byte)0xE2, (byte)0xB0, 0x40, (byte)0xC1, (byte)0xE1, 0x30, 0x51, 
      (byte)0x80, (byte)0xE5, 0x01, 0x4A, (byte)0xA0, (byte)0xE3, (byte)0xB2, 0x40, (byte)0xC1, (byte)0xE1, (byte)0xB0, 0x60, (byte)0xC0, (byte)0xE1, (byte)0xFC, 0x03, (byte)0xBD, (byte)0xE8, (byte)0x84, 0x30, (byte)0x80, (byte)0xE5, (byte)0x80, 0x10, (byte)0x80, (byte)0xE2, 
      (byte)0xFC, 0x03, (byte)0xA1, (byte)0xE8, 0x60, 0x10, (byte)0x80, (byte)0xE2, (byte)0xFC, 0x03, (byte)0xBD, (byte)0xE8, (byte)0xFC, 0x03, (byte)0xA1, (byte)0xE8, (byte)0xF0, 0x4F, (byte)0xBD, (byte)0xE8, (byte)0xB6, 0x10, (byte)0xD0, (byte)0xE1, (byte)0xA0, 0x00, 
      0x51, (byte)0xE3, (byte)0xFC, (byte)0xFF, (byte)0xFF, 0x1A, 0x4C, (byte)0xF0, 0x10, (byte)0xE5, 0x00, 0x12, (byte)0x90, (byte)0xE5, 0x01, 0x08, 0x11, (byte)0xE3, 0x01, 0x02, 0x11, 0x03, 0x4C, (byte)0xF0, 0x10, 0x05, 
      0x48, (byte)0xF0, 0x10, (byte)0xE5, (byte)0xA0, 0x7F, 0x00, 0x03, 0x00, 0x00, 0x0C, 0x43, 0x00, 0x10, (byte)0xFF, (byte)0xFF};
  
  private static final int ACTIVATION_LENGTH = 36;
  
  private class InterruptHandler
  {
    boolean thumb;
    int rd, rn;
    int rdData;
    int rnData;
    int rdAddr;
    int rnAddr;
    int strAddress;
    
    InterruptHandler(boolean thumb, int rd, int rn, int rdData, int rnData, int rdAddr, int rnAddr, int strAddress)
    {
      this.thumb = thumb;
      this.rd = rd;
      this.rn = rn;
      this.rdData = rdData;
      this.rnData = rnData;
      this.rdAddr = rdAddr;
      this.rnAddr = rnAddr;
      this.strAddress = strAddress;
    }
    
    public String toString()
    {
      return String.format(
          "Handler (thumb=%b)\n"+
          "  0x%08x: r%d=0x%08x\n"+
          "  0x%08x: r%d=0x%08x\n"+
          "  0x%08x: str r%d, [r%d]\n"
          , thumb, rdAddr, rd, rdData, rnAddr, rn, rnData, strAddress, rd, rn);
    }
    
    void hookHandler()
    {
      System.out.print(toString());
      
      if (!thumb)
        buffer.writeU32(0xE12FFF10 + rn, strAddress);
      else
        buffer.writeU16(0x4700 + (rn << THUMB_STR_RB_SHIFT), strAddress);

      buffer.writeU32(0x08000000 + (int)hookLocation, rnAddr);
      
      System.out.printf("Writing 0x%04x at 0x0%08x\n", 0x4700 + (rn << THUMB_STR_RB_SHIFT), strAddress);
      System.out.printf("Writing 0x%08x at 0x0%08x\n", 0x08000000 + (int)hookLocation, rnAddr);
      
      writeActivation();
      hookLocation += ACTIVATION_LENGTH;
    }
    
    void writeActivation()
    {
      buffer.position((int)hookLocation);
      
      buffer.writeU32(0xE92D5003);
      buffer.writeU32(0xE1A0C000 + rd);
      buffer.writeU32(0xE1A01000 + rn);
      buffer.writeU32(0xE1A0000C);
      buffer.writeU32(0xEB000000 + 0x00FFFFFF & ((((int)patchLocation) - ((int)hookLocation+8+16)) >> 2) );
      buffer.writeU32(0xE8BD5003);
      buffer.writeU32(0xE59F0000 + (rn << REG_DEST_SHIFT));
      buffer.writeU32(0xE12FFF10 + rn);
      buffer.writeU32(0x08000000 + strAddress+1+2);
    }
  }
  
  private BinaryBuffer buffer;
  private List<InterruptHandler> handlers;
  private long patchLocation;
  private long hookLocation;
  
  public GBASleepHack()
  {
    handlers = new ArrayList<>();
  }
  
  public void setup() throws IOException
  {
    patchLocation = buffer.length() - 1;
    byte value = buffer.read(patchLocation);
    byte firstValue = value;
    
    // find first unused byte starting from end
    while (value == firstValue)//(value == 0x00 || value == (byte)0xff)
      value = buffer.read(--patchLocation);
    
    ++patchLocation;
    
    // adjust position to be aligned to 16 bytes
    if (patchLocation % 16 != 0)
      patchLocation += 16 - (patchLocation % 16);
    hookLocation = patchLocation + patch.length;
  }
  
  public void adjustFileSize() throws IOException
  {
    long size = buffer.length() + patch.length + handlers.size()*ACTIVATION_LENGTH;
    
    if (size > buffer.length())
    {
      if (size % 256 != 0)
        size += 256 - size%256;
    }
    
    buffer.resize(size);
  }
  
  public void writePayload() throws IOException
  {
    buffer.write(patch, (int)patchLocation);
  }
  
  public void patch(BinaryBuffer buffer) throws IOException
  {
    this.buffer = buffer;
    resetBuffers();
    handlers.clear();

    setup();
    findHandlers();
    adjustFileSize();
    writePayload();


    for (InterruptHandler handler : handlers)
      handler.hookHandler();
  }
  
  private void resetBuffers()
  {
    Arrays.fill(regData, 0);
    Arrays.fill(regAddr, 0);
    Arrays.fill(regLastWrite, 0);
  }
  
  private void findHandlers() throws IOException
  {
    handlers = new ArrayList<>();
    buffer.position(0);
    
    while (buffer.position() < buffer.length() - 4)
    {
      int word = buffer.readU32();
            
      if ((word & LDR_MASK) == LDR_BITS)
      {
        int pc = buffer.position() + 8 - 4;
        int offsetSign = (word & UP_DOWN_BIT_MASK) != 0 ? 1 : -1;
        
        int rd = (word >> REG_DEST_SHIFT) & REG_MASK;
        int offset = word & OFFSET_MASK;
        
        int address = offsetSign*offset + pc;
        
        int memdata = 0xDEADBEEF;
        
        if (address < buffer.length() - 3 && address % 4 == 0)
          memdata = buffer.readU32(address);
        
        regData[rd] = memdata;
        regAddr[rd] = address;
        regLastWrite[rd] = buffer.position() - 4;
      }
      else if ((word & STR_MASK) == STR_BITS)
      {
        int rn = (word >> REG_SRC_SHIFT) & REG_MASK;
        int rd = (word >> REG_DEST_SHIFT) & REG_MASK;
        
        int myaddress = buffer.position() - 4;
        
        boolean okay = true
            && (regData[rn] == 0x03007FFC)
            && (myaddress - regLastWrite[rd] < 64)
            && (myaddress - regLastWrite[rn] < 64)
            && (regLastWrite[rd] != 0) && (regLastWrite[rn] != 0)
            && ((regData[rd] & 0xFF000000) == 0x03000000);
        
        if (!okay && regData[rn] == 0x03007FFC && myaddress == 0xE0)
          okay = true;
        
        if (myaddress < regLastWrite[rd] || myaddress < regLastWrite[rn])
          okay = false;
        
        if (okay)
        {
          InterruptHandler intHandler = new InterruptHandler(false,rd,rn,regData[rd],regData[rn],regAddr[rd],regAddr[rn],myaddress);
          handlers.add(intHandler);
        }
      }
    }
    
    buffer.position(0);
    resetBuffers();
    
    while (buffer.position() < buffer.length()-2)
    {
      int word = buffer.readU16();
      
      if ((word & THUMB_LDR_MASK) == THUMB_LDR_BITS)
      {
        //System.out.printf("Found LDR AT %08x - %04x", buffer.position() - 2, word);
        
        int pc = buffer.position() + 4 - 2;
        
        int rd = (word >> THUMB_LDR_RD_SHIFT) & THUMB_REG_MASK;
        int offset = word & THUMB_OFFSET_MASK;
        
        int address = offset*4 + pc;
        //System.out.printf(" address %08x\n", address);

        int memdata = 0xDEADBEEF;
        address -= address % 4;
        
        if (address < buffer.length() - 3)
          memdata = buffer.readU32(address);
        
        //System.out.printf("pc + offset * 4 = 0x%08x + %d * 4 = 0x%08x\n", pc, offset, offset*4+pc);

        regData[rd] = memdata;
        regAddr[rd] = address;
        regLastWrite[rd] = buffer.position() - 2;
      }
      else if ((word & THUMB_STR_MASK) == THUMB_STR_BITS)
      {
        int rb = (word >> THUMB_STR_RB_SHIFT) & THUMB_REG_MASK;
        int rd = (word >> THUMB_STR_RD_SHIFT) & THUMB_REG_MASK;

        int myaddress = buffer.position() - 2;
        
        //System.out.printf(" address %08x\n", myaddress);
        //System.out.printf("Conditions %08x %d %d\n",regData[rb],myaddress - regLastWrite[rd],myaddress - regLastWrite[rb]);
     
        boolean okay = true
            && (regData[rb] == 0x03007FFC)
            && (myaddress - regLastWrite[rd] < 64)
            && (myaddress - regLastWrite[rb] < 64)
            && (regLastWrite[rd] != 0) && (regLastWrite[rb] != 0)
            && ((regData[rd] & 0xFF000000) == 0x03000000);
        
        if (okay)
        {
          System.out.printf("Found Handler %08x %08x\n", regAddr[rd], regAddr[rb]);
          InterruptHandler intHandler = new InterruptHandler(true,rd,rb,regData[rd],regData[rb],regAddr[rd],regAddr[rb],myaddress);
          handlers.add(intHandler);
        }
      }
    }
  }
}
