package com.pixbits.io;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashSet;
import java.util.Set;

public class FolderScanner
{
  final Set<Path> files;
  final Set<Path> excluded;
  final PathMatcher filter;
  
  public FolderScanner(PathMatcher filter, Set<Path> excluded)
  {
    files = new HashSet<Path>();
    this.filter = filter;
    this.excluded = excluded;
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
        if (!excluded.stream().anyMatch(path -> e.startsWith(path)))
        {
          if (Files.isDirectory(e))
            innerScan(e);
          else if (filter.matches(e.getFileName()))
            files.add(e);
        }
      });
    }
    catch (IOException e)
    {
      e.printStackTrace();
      //TODO: log
    }
  }
}
