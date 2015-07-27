package jack.rm.json.workflow;

import jack.rm.data.Rom;
import jack.rm.data.RomPath;

import java.nio.ByteOrder;

import com.pixbits.io.BinaryBuffer;
import com.pixbits.workflow.WorkflowData;

public class RomHandle implements WorkflowData
{
  private final Rom rom;
  private BinaryBuffer buffer;
  private RomPath path;


  public RomHandle(Rom rom)
  {
    this(rom, rom.getPath());
  }
  
  public RomHandle(Rom rom, RomPath path)
  {
    this.rom = rom;
    this.path = path;
    this.buffer = null;
  }
  
  public Rom getRom() { return rom; }
  public BinaryBuffer getBuffer()
  {
    if (buffer == null)
    {
      try
      {
        buffer = new BinaryBuffer(rom.getPath().file(), BinaryBuffer.Mode.WRITE, ByteOrder.BIG_ENDIAN);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    return buffer;
  }
  
  public RomPath getPath() { return path; }
}