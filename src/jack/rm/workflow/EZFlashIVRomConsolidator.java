package jack.rm.workflow;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.GBA;
import com.pixbits.workflow.Dumper;

import jack.rm.data.rom.Rom;

public class EZFlashIVRomConsolidator extends Dumper<RomWorkflowEntry>
{
  Path destination;
  
  public EZFlashIVRomConsolidator(Path destination)
  {
    this.destination = destination;
  }
  
  public void accept(RomWorkflowEntry handle)
  {
    try
    {
      Rom rom = handle.getRom();
      Path finalBasePath = destination.resolve(handle.getDestPath());
      Path finalPath = finalBasePath.resolve(rom.getTitle()+"."+rom.getSystem().exts[0]);
      Files.createDirectories(finalBasePath);
      
      if (handle.getPath() == null)
        handle.getBuffer();
      
      Files.move(handle.getPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
      //Files.deleteIfExists(handle.getPath());
      
      Path saverPath = destination.resolve(Paths.get("SAVER")).resolve(rom.getTitle()+".sav");
      
      GBA.Save save = rom.getAttribute(GameAttribute.SAVE_TYPE);
      
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
