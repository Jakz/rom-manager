package jack.rm.workflow;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.io.BinaryBuffer;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.workflow.WorkflowData;

public class RomWorkflowEntry implements WorkflowData
{
  private final Game rom;
  private BinaryBuffer buffer;
  private Path path;
  private Path destPath;


  public RomWorkflowEntry(Game rom)
  {
    this.rom = rom;
    destPath = Paths.get(".");
  }

  public Game getGame() { return rom; }
  
  public void prepareBuffer() throws IOException
  {
    Handle source = getGame().rom().handle();
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