package jack.rm.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.pixbits.lib.workflow.Dumper;

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
    Path finalPath = destination.resolve(handle.getGame().rom().handle().path().getFileName());
    
    try
    {
      if (overwrite || !Files.exists(finalPath))
        Files.copy(handle.getGame().rom().handle().path(), finalPath);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
