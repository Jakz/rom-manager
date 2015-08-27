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
  boolean overwrite;

  
  public RomConsolidator(Path path)
  {
    this.destination = path;
    this.overwrite = false;
  }
  
  public void accept(RomHandle handle)
  {
    try
    {
      Rom rom = handle.getRom();
      Path basePath = destination.resolve(handle.getDestPath());
      Path finalPath = basePath.resolve(rom.getTitle()+"."+rom.getSystem().exts[0]);
      
      if (!overwrite && Files.exists(finalPath))
        return;
      
      if (handle.getBuffer() == null)
        handle.prepareBuffer();
      
      Files.createDirectories(basePath);
      Files.move(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      //Files.deleteIfExists(handle.getPath());
    }
    catch (Exception e)
    {
      System.out.println("Error on "+handle.getRom().getPath().toString());
      e.printStackTrace();
    }
  }
}
