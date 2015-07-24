package jack.rm.plugins.cleanup;

import java.util.zip.ZipEntry;
import java.nio.file.Path;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import com.pixbits.plugin.ExposedParameter;
import com.pixbits.stream.StreamException;

import jack.rm.data.RomList;
import jack.rm.data.RomPath;
import jack.rm.data.RomStatus;

public class ArchiveMergerPlugin extends CleanupPlugin
{
  @ExposedParameter(name="Archive Path", description="This is the archive that will contain the whole romset")
  Path path; 
  
  @Override public void execute(RomList list)
  {
    try
    {
      ZipFile zfile = new ZipFile(list.set.getSettings().romsPath.resolve("romset.zip").toFile());
      ZipParameters zparams = new ZipParameters(), aparams = new ZipParameters();
      
      
      zparams.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
      zparams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
      
      aparams.setCompressionLevel(Zip4jConstants.COMP_DEFLATE);
      aparams.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
      aparams.setSourceExternalStream(true);
      
      list.stream()
      .filter( r -> r.status != RomStatus.MISSING )
      .forEach(StreamException.rethrowConsumer( r -> {
        if (!r.getPath().isArchive())
        {
          zfile.addFile(r.getPath().file().toFile(), zparams);
        }
        else
        {
          String fileName = r.getPath().file().getFileName().toString();
          aparams.setFileNameInZip(fileName);
          
          try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(r.getPath().file().toFile()))
          {
            ZipEntry entry = zip.getEntry(((RomPath.Archive)r.getPath()).internalName);         
            zfile.addStream(zip.getInputStream(entry), aparams);
          }
        }
      }));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

  }
  
  @Override public String getMenuCaption() { return "Archive RomSet"; }
}
