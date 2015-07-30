package jack.rm.workflow;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import com.pixbits.workflow.*;

import jack.rm.data.console.GBA;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;

public class EZFlashIVRomConsolidator extends Dumper<RomHandle>
{
  Path destination = Paths.get("/Users/jack/Documents/dev/gba/ez");
  
  public void accept(RomHandle handle)
  {
    try
    {
      Rom rom = handle.getRom();
      Path finalBasePath = destination.resolve(handle.getDestPath());
      Path finalPath = finalBasePath.resolve(rom.getTitle()+"."+rom.getSystem().exts[0]);
      Files.createDirectories(finalBasePath);
      Files.move(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      //Files.deleteIfExists(handle.getPath());
      
      Path saverPath = destination.resolve(Paths.get("SAVER")).resolve(rom.getTitle()+".sav");
      
      GBA.Save save = rom.getAttribute(RomAttribute.SAVE_TYPE);
      
      if (!Files.exists(saverPath) && save.getSize() != 0)
      {
        Files.createDirectories(destination.resolve(Paths.get("SAVER")));
        
        long fsize = Math.max(save.getSize(), 8192);
        
        OutputStream wrt = Files.newOutputStream(saverPath, StandardOpenOption.CREATE);
        for (int i = 0; i < fsize; ++i)
          wrt.write(0xFF);
        
        wrt.close();
      }
      
      
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
