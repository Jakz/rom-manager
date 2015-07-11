package jack.rm.files;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.HashSet;

public class FolderScanner
{
  final Set<Path> files;
  final PathMatcher filter;
  
  public FolderScanner(PathMatcher filter)
  {
    files = new HashSet<Path>();
    this.filter = filter;
  }
  
  public Set<Path> scan(Path root)
  {
    if (Files.isDirectory(root))
      innerScan(root);
    
    return files;
  }
  
  private void innerScan(Path folder)
  {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder))
    {
      stream.forEach( e ->
      {                
        if (Files.isDirectory(e))
           innerScan(e);
        else if (filter.matches(e.getFileName()))
          files.add(e);
      });
    }
    catch (IOException e)
    {
      e.printStackTrace();
      //TODO: log
    }
  }
}
