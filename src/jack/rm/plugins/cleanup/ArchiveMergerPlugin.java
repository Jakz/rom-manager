package jack.rm.plugins.cleanup;

import java.util.function.Consumer;
import java.util.zip.ZipEntry;

import java.nio.file.Path;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import com.pixbits.plugin.ExposedParameter;
import jack.rm.data.Rom;
import jack.rm.data.RomList;
import jack.rm.data.RomPath;
import jack.rm.data.RomStatus;
import jack.rm.data.set.RomSet;
import jack.rm.files.OrganizerWorker;
import jack.rm.plugins.BackgroundPlugin;

public class ArchiveMergerPlugin extends CleanupPlugin implements BackgroundPlugin
{
  @ExposedParameter(name="Archive Path", description="This is the archive that will contain the whole romset")
  Path path; 
  
  @Override public void execute(RomList list)
  {
    new ArchiverWorker(list.set, this, b -> {}).execute();
  }
  
  @Override public String getTitle() { return "Archiving romset"; }
  @Override public String getProgressText() { return "Archiving..."; }
  @Override public String getMenuCaption() { return "Archive RomSet"; }
  
  public class ArchiverWorker extends OrganizerWorker<ArchiveMergerPlugin>
  {
    ZipFile zfile = null;
    ZipParameters zparams = null, aparams = null;

    public ArchiverWorker(RomSet romSet, ArchiveMergerPlugin plugin, Consumer<Boolean> callback)
    {
      super(romSet, plugin, callback);
      
      try
      {
        zfile = new ZipFile(romSet.list.set.getSettings().romsPath.resolve("romset.zip").toFile());
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
    public void execute(Rom r)
    {
      try
      {
        if (r.status != RomStatus.MISSING)
        {        
  
          if (!r.getPath().isArchive())
          {
            zfile.addFile(r.getPath().file().toFile(), zparams);
          }
          else
          {
            String fileName = r.getPath().file().getFileName().toString();
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            fileName = fileName + "." + romSet.system.exts[0];
            
            aparams.setFileNameInZip(fileName);
            
            try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(r.getPath().file().toFile()))
            {
              ZipEntry entry = zip.getEntry(((RomPath.Archive)r.getPath()).internalName);         
              zfile.addStream(zip.getInputStream(entry), aparams);
            }
          }     
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

  }
}
