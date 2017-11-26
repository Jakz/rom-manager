package jack.rm.plugins.cleanup;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.plugin.ExposedParameter;

import jack.rm.data.romset.Settings;
import jack.rm.plugins.PluginWithIgnorePaths;
import jack.rm.plugins.types.CleanupPlugin;

public class MoveUnknownFilesPlugin extends CleanupPlugin implements PluginWithIgnorePaths
{
  @ExposedParameter(name="Unknown Path", description="The folder to move unknown file into, can be inside the romset path", params="directories") Path path;
  
  int counter;
 
  @Override public void execute(GameSet set)
  {
    try
    {  
      counter = 0; 
      if (!Files.exists(path) || !Files.isDirectory(path))
        Files.createDirectory(path);
  
      Set<Path> existing = set.romStream()
        .filter(r -> r.isPresent())
        .map(r -> r.handle().path())
        .collect(Collectors.toSet());
  
      Settings settings = getGameSetSettings();
      Set<Path> total = new FolderScanner(FileSystems.getDefault().getPathMatcher("glob:*.*"), settings.getIgnoredPaths(), true).scan(settings.romsPath);
      
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
  
  @Override public String getSubmenuCaption() { return "Cleanup"; }
  @Override public String getMenuCaption() { return "Move unrecognized files"; }

}
