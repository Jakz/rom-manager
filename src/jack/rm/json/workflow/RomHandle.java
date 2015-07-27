package jack.rm.json.workflow;

import jack.rm.data.Rom;
import jack.rm.data.RomPath;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import com.pixbits.io.BinaryBuffer;
import com.pixbits.workflow.WorkflowData;

public class RomHandle implements WorkflowData
{
  private final Rom rom;
  private BinaryBuffer buffer;
  private Path path;


  public RomHandle(Rom rom)
  {
    this.rom = rom;
  }

  public Rom getRom() { return rom; }
  
  private void prepareBuffer() throws IOException
  {
    RomPath source = rom.getPath();
    this.path = Files.createTempFile(null, null);
    Files.copy(source.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
  }
  
  public BinaryBuffer getBuffer()
  {
    if (buffer == null)
    {
      try
      {
        prepareBuffer();
        buffer = new BinaryBuffer(path, BinaryBuffer.Mode.WRITE, ByteOrder.BIG_ENDIAN);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    return buffer;
  }
  
  public Path getPath() { return path; }
}