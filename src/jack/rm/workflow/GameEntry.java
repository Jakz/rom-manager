package jack.rm.workflow;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.io.BinaryBuffer;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.workflow.WorkflowData;

public class GameEntry implements WorkflowData
{
  private final Game game;
  private BinaryBuffer buffer;
  private Path path;
  
  private Supplier<String> fileName;
  private Supplier<Path> folder;
  
  public GameEntry(Game game)
  {
    this.game = game;
    this.fileName = () -> game.getTitle() + "." + game.getPlatform().exts[0];
    this.folder = () -> Paths.get(".");
  }

  public Game getGame() { return game; }
  
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
  
  public Supplier<String> getFileName() { return fileName; }
  public void setFileName(Supplier<String> fileName) { this.fileName = fileName; } 
  
  public Supplier<Path> getFolder() { return folder; }
  public void setFolder(Supplier<Path> folder) { this.folder = folder; }
  
  public Path getFinalPath(Path base)
  {
    return base.resolve(folder.get()).resolve(fileName.get());
  }

  public Path getPath() { return path; }
}