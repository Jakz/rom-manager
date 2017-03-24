package jack.rm.plugins.cleanup;

import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.concurrent.OperationDetails;
import com.pixbits.lib.plugin.ExposedParameter;

import jack.rm.files.RomSetWorker;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class ArchiveMergerPlugin extends CleanupPlugin implements OperationDetails
{
  @ExposedParameter(name="Archive Path", description="This is the archive that will contain the whole romset", params="files")
  Path path; 
  
  @Override public void execute(GameSet set)
  {
    new ArchiverWorker(set, this, b -> {}).execute();
  }
  
  @Override public String getTitle() { return "Archiving romset"; }
  @Override public String getProgressText() { return "Archiving..."; }
  @Override public String getMenuCaption() { return "Archive RomSet"; }
  @Override public String getSubmenuCaption() { return "Cleanup"; }
  
  public class ArchiverWorker extends RomSetWorker<ArchiveMergerPlugin>
  {
    ZipFile zfile = null;
    ZipParameters zparams = null, aparams = null;

    public ArchiverWorker(GameSet romSet, ArchiveMergerPlugin plugin, Consumer<Boolean> callback)
    {
      super(romSet, plugin, r -> r.getStatus().isComplete(), callback);
      
      try
      {
        zfile = new ZipFile(romSet.getSettings().romsPath.resolve("romset.zip").toFile());
        zparams = new ZipParameters();
        aparams = new ZipParameters();
      
        zparams.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        zparams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        
        aparams.setCompressionLevel(Zip4jConstants.COMP_DEFLATE);
        aparams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        aparams.setSourceExternalStream(true);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    @Override
    public void execute(Game r)
    {
      // TODO: rewrite
      /*try
      {
        if (!r.getHandle().isArchive())
        {
          zfile.addFile(r.getHandle().path().toFile(), zparams);
        }
        else
        {
          String fileName = r.getHandle().path().getFileName().toString();
          fileName = fileName.substring(0, fileName.lastIndexOf('.'));
          fileName = fileName + "." + romSet.platform.exts[0];
          
          aparams.setSourceExternalStream(true);
          aparams.setFileNameInZip(fileName);
       
          zfile.addStream(r.getHandle().getInputStream(), aparams);
        }     
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }*/
    }
  }
}
