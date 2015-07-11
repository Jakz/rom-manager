package jack.rm.files;

import java.io.File;
import java.io.FileFilter;
import java.util.Set;
import java.util.HashSet;

public class FolderScanner
{
  final Set<File> files;
  final FileFilter filter;
  
  public FolderScanner(FileFilter filter)
  {
    files = new HashSet<File>();
    this.filter = filter;
  }
  
  public Set<File> scan(File root)
  {
    if (root.isDirectory())
      innerScan(root);
    
    return files;
  }
  
  private void innerScan(File folder)
  {
    File[] files = folder.listFiles(filter);
        
    for (int t = 0; t < files.length; ++t)
    {
      if (files[t].isDirectory())
      {
        innerScan(files[t].getAbsoluteFile());
      }
      else 
      {
        this.files.add(files[t]);
      }
    }
  }
}
