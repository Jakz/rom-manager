package jack.rm.workflow;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import com.pixbits.workflow.Dumper;

import jack.rm.data.rom.Rom;
import jack.rm.files.romhandles.RomPath;

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
      
      Files.createDirectories(basePath);
      
      if (handle.hasBeenModified())
      {
        Files.move(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      }
      else
      {
        RomPath path = rom.getPath();
        
        if (!path.isArchive())
          Files.copy(path.file(), finalPath, StandardCopyOption.REPLACE_EXISTING);
        else
          Files.copy(path.getInputStream(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      }
      
      //if (handle.getBuffer() == null)
      //  handle.prepareBuffer();
      

      //Files.deleteIfExists(handle.getPath());
    }
    catch (Exception e)
    {
      System.out.println("Error on "+handle.getRom().getPath().toString());
      if (e.getCause() != e)
        e.getCause().printStackTrace();
      else
        e.printStackTrace();
    }
  }
}
