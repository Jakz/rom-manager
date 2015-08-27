package jack.rm.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.pixbits.workflow.Dumper;

public class RomExportConsolidator extends Dumper<RomHandle>
{
  Path destination;
  boolean overwrite;
  
  public RomExportConsolidator(Path destination)
  {
    this.destination = destination;
    this.overwrite = false;
  }
  
  public void accept(RomHandle handle)
  {
    Path finalPath = destination.resolve(handle.getRom().getPath().file().getFileName());
    
    try
    {
      if (overwrite || !Files.exists(finalPath))
        Files.copy(handle.getRom().getPath().file(), finalPath);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
