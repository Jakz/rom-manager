package jack.rm.workflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.pixbits.workflow.*;

import jack.rm.data.rom.Rom;

public class RomConsolidator extends Dumper<RomHandle>
{
  Path destination;
  
  public RomConsolidator(Path path)
  {
    this.destination = path;
  }
  
  public void accept(RomHandle handle)
  {
    try
    {
      if (handle.getBuffer() == null)
        handle.prepareBuffer();
      
      Rom rom = handle.getRom();
      Path basePath = destination.resolve(handle.getDestPath());
      Path finalPath = basePath.resolve(rom.getTitle()+"."+rom.getSystem().exts[0]);
      Files.createDirectories(basePath);
      Files.move(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      //Files.deleteIfExists(handle.getPath());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
