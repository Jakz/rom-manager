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
  
  private static class InterruptHandler
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
  }
  
  private static List<InterruptHandler> handlers;
  
  public static void patch(BinaryBuffer buffer) throws IOException
  {
    findHandlers(buffer);
    
    for (InterruptHandler handler : handlers)
    {
      System.out.print(handler.toString());
    }
  }
  
  private static void resetBuffers()
  {
    Arrays.fill(regData, 0);
    Arrays.fill(regAddr, 0);
    Arrays.fill(regLastWrite, 0);
  }
  
  private static void findHandlers(BinaryBuffer buffer) throws IOException
  {
    handlers = new ArrayList<>();
    
    buffer.position(0);
    resetBuffers();
    
    while (!buffer.didReachEnd())
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
          InterruptHandler intHandler = new InterruptHandler(false,rd,rn,regData[rd],regData[rn],regLastWrite[rd],regLastWrite[rn],myaddress);
          handlers.add(intHandler);
        }
      }
    }
    
    buffer.position(0);
    resetBuffers();
    
    while (!buffer.didReachEnd())
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

        regData[rd] = memdata;
        regAddr[rd] = address;
        regLastWrite[rd] = buffer.position() - 2;
      }
      else if ((word & THUMB_STR_MASK) == THUMB_STR_BITS)
      {
        int rb = (word >> THUMB_STR_RB_SHIFT) & THUMB_REG_MASK;
        int rd = (word >> THUMB_STR_RD_SHIFT) & THUMB_REG_MASK;
        
        System.out.printf("Found STR AT %08x - %04x - ", buffer.position() - 2, word);
      
        int myaddress = buffer.position() - 2;
        
        System.out.printf(" address %08x\n", myaddress);
        System.out.printf("Conditions %08x %d %d\n",regData[rb],myaddress - regLastWrite[rd],myaddress - regLastWrite[rb]);
     
        boolean okay = true
            && (regData[rb] == 0x03007FFC)
            && (myaddress - regLastWrite[rd] < 64)
            && (myaddress - regLastWrite[rb] < 64)
            && (regLastWrite[rd] != 0) && (regLastWrite[rb] != 0)
            && ((regData[rd] & 0xFF000000) == 0x03000000);
        
        if (okay)
        {
          InterruptHandler intHandler = new InterruptHandler(true,rd,rb,regData[rd],regData[rb],regLastWrite[rd],regLastWrite[rb],myaddress);
          handlers.add(intHandler);
        }
      }
    }
  }
}
