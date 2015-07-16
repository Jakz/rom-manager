package jack.rm.plugins.rom;

import java.io.IOException;

import com.pixbits.io.BinaryBuffer;

public class GBASavePatcherPlugin
{
  private static class PatchEntry
  {
    byte[] fromBlock;
    byte[] toBlock;
    
    PatchEntry(byte[] fromBlock, byte[] toBlock)
    {
      this.fromBlock = fromBlock;
      this.toBlock = toBlock;
    }
    
    void patch(BinaryBuffer buffer) throws IOException
    {
      buffer.replace(fromBlock, toBlock);
    }
  }
  
  private static final PatchEntry[] Flash_v120_v121 = {
    new PatchEntry(
        new byte[] { (byte)0x90, (byte)0xb5, (byte)0x93, (byte)0xb0, 0x6f, 0x46, 0x39, 0x1d, 0x08, 0x1c, 0x00, (byte)0xf0 },
        new byte[] { 0x00, (byte)0xb5, (byte)0x3d, 0x20, 0x00, 0x02, 0x1f, 0x21, 0x08, 0x43, 0x02, (byte)0xbc, 0x08, 0x47 }
    )
  };
}
