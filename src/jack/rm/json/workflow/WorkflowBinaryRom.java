package jack.rm.json.workflow;

import java.nio.ByteOrder;

import jack.rm.data.Rom;

import com.pixbits.workflow.*;
import com.pixbits.io.*;

public class WorkflowBinaryRom extends WokrflowRom
{
  private BinaryBuffer buffer;
  
  WorkflowBinaryRom(Rom rom)
  {
    super(rom);
    try
    {
      buffer = new BinaryBuffer(rom.getPath().file(), BinaryBuffer.Mode.WRITE, ByteOrder.BIG_ENDIAN);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public BinaryBuffer getBuffer() { return buffer; }
}
