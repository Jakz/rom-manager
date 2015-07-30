package jack.rm.json.workflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.pixbits.workflow.*;

import jack.rm.data.rom.Rom;

public class RomConsolidator extends Dumper<RomHandle>
{
  Path destination = Paths.get("/Users/jack/Documents/dev/ez");
  
  public void accept(RomHandle handle)
  {
    try
    {
      Rom rom = handle.getRom();
      Path finalPath = destination.resolve(rom.getTitle()+"."+rom.getSystem().exts[0]);
      Files.copy(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      Files.deleteIfExists(handle.getPath());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
