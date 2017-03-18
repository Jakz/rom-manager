package jack.rm.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.pixbits.workflow.Dumper;

public class RomExportConsolidator extends Dumper<RomWorkflowEntry>
{
  Path destination;
  boolean overwrite;
  
  public RomExportConsolidator(Path destination)
  {
    this.destination = destination;
    this.overwrite = false;
  }
  
  public void accept(RomWorkflowEntry handle)
  {
    Path finalPath = destination.resolve(handle.getRom().getHandle().path().getFileName());
    
    try
    {
      if (overwrite || !Files.exists(finalPath))
        Files.copy(handle.getRom().getHandle().path(), finalPath);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
