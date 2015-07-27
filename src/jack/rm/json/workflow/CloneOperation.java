package jack.rm.json.workflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jack.rm.data.RomPath;

public class CloneOperation implements RomOperation
{
  public String getName() { return "Copy ROM"; }
  public String getDescription() { return "This operation creates a clone of the ROM to be able to alter it"; }

  public CloneOperation()
  {

  }
  
  public RomHandle apply(RomHandle rom)
  {
    try
    {
      if (rom.getBuffer() != null)
        rom.getBuffer().close();
      
      RomPath source = rom.getPath();
      Path tmp = Files.createTempFile(null, null);
      RomPath dest = source.build(tmp);
      Files.copy(source.file(), dest.file());
      return new RomHandle(rom.getRom(), dest);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
}
