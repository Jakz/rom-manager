package jack.rm.plugins.cleanup;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import jack.rm.Settings;
import jack.rm.data.RomList;
import jack.rm.data.RomStatus;
import jack.rm.files.FolderScanner;
import jack.rm.plugin.ExposedParameter;
import jack.rm.plugins.PluginWithIgnorePaths;

public class MoveUnknownFilesPlugin extends CleanupPlugin implements PluginWithIgnorePaths
{
  @ExposedParameter
  Path path;
  
  @Override public void execute(RomList list)
  {
    try
    {  
      if (!Files.exists(path) || !Files.isDirectory(path))
        Files.createDirectory(path);
  
      Set<Path> existing = list.stream()
        .filter( r -> r.status != RomStatus.NOT_FOUND )
        .map( r -> r.entry.file())
        .collect(Collectors.toSet());
  
      Set<Path> total = new FolderScanner(FileSystems.getDefault().getPathMatcher("glob:*.*"), Settings.current().getIgnoredPaths()).scan(Settings.current().romsPath);
      
      total.removeAll(existing);
      
      total.stream()
        .filter( f -> !f.getParent().equals(path) )
        .forEach( f -> {
          Path dest = path.resolve(f.getFileName());
          int i = 1;
          
          while (Files.exists(dest))
            dest = path.resolve(f.getFileName().toString()+(i++));
  
          try { Files.move(f, dest); }
          catch (IOException e) { e.printStackTrace(); /* TODO: log */ }
   
        }); 
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
}
