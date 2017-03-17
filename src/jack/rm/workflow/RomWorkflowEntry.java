package jack.rm.workflow;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.pixbits.lib.io.BinaryBuffer;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.workflow.WorkflowData;

import jack.rm.data.rom.Rom;

public class RomWorkflowEntry implements WorkflowData
{
  private final Rom rom;
  private BinaryBuffer buffer;
  private Path path;
  private Path destPath;


  public RomWorkflowEntry(Rom rom)
  {
    this.rom = rom;
    destPath = Paths.get(".");
  }

  public Rom getRom() { return rom; }
  
  public void prepareBuffer() throws IOException
  {
    Handle source = rom.getPath();
    this.path = Files.createTempFile(null, null);
    Files.copy(source.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
  }
  
  public BinaryBuffer getBuffer() throws Exception
  {
    if (buffer == null)
    {
      prepareBuffer();
      buffer = new BinaryBuffer(path, BinaryBuffer.Mode.WRITE, ByteOrder.BIG_ENDIAN);
    }
    
    return buffer;
  }
  
  public boolean hasBeenModified() { return buffer != null; }
  
  public Path getDestPath() { return destPath; }
  public void setDestPath(Path path) { this.destPath = path; }
  
  public Path getPath() { return path; }
}