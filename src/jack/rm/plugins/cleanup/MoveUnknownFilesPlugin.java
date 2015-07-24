package jack.rm.plugins.cleanup;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.pixbits.plugin.ExposedParameter;

import jack.rm.Settings;
import jack.rm.data.RomList;
import jack.rm.data.RomStatus;
import jack.rm.files.FolderScanner;
import jack.rm.plugins.PluginWithIgnorePaths;

public class MoveUnknownFilesPlugin extends CleanupPlugin implements PluginWithIgnorePaths
{
  @ExposedParameter(name="Unknown Path", description="The folder to move unknown file into, can be inside the romset path", params="directories") Path path;
  
  int counter;
 
  @Override public void execute(RomList list)
  {
    try
    {  
      counter = 0; 
      if (!Files.exists(path) || !Files.isDirectory(path))
        Files.createDirectory(path);
  
      Set<Path> existing = list.stream()
        .filter( r -> r.status != RomStatus.MISSING )
        .map( r -> r.getPath().file())
        .collect(Collectors.toSet());
  
      Settings settings = list.set.getSettings();
      Set<Path> total = new FolderScanner(FileSystems.getDefault().getPathMatcher("glob:*.*"), settings.getIgnoredPaths()).scan(settings.romsPath);
      
      total.removeAll(existing);
      
      total.stream()
        .filter( f -> !f.getParent().equals(path) )
        .forEach( f -> {
          Path dest = path.resolve(f.getFileName());
          int i = 1;
          
          while (Files.exists(dest))
            dest = path.resolve(f.getFileName().toString()+(i++));
  
          try { Files.move(f, dest); ++counter; }
          catch (IOException e) { e.printStackTrace(); /* TODO: log */ }
   
        }); 
      
      message("Moved "+counter+" unknown files");
    }
    catch (IOException e)
    {
      e.printStackTrace();
      // TODO: log
    }
  }

  @Override
  public Set<Path> getIgnoredPaths()
  {
    return new HashSet<Path>(Arrays.asList(path));
  }
  
  @Override public String getMenuCaption() { return "Move unrecognized files"; }

}
