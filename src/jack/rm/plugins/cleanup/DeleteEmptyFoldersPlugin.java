package jack.rm.plugins.cleanup;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import jack.rm.data.RomList;

public class DeleteEmptyFoldersPlugin extends CleanupPlugin
{
  @Override public void execute(RomList list)
  {
    Queue<File> files = new LinkedList<File>();
    files.add(list.set.romPath().toFile());
    
    int counter = 0;
    
    while (!files.isEmpty())
    {
      File f = files.poll();
      File[] l = f.listFiles();

      for (File ff : l)
      {
        if (ff.isDirectory())
        {
          if (ff.listFiles().length == 0)
          {
            ff.delete();
            ++counter;
          }
          else
            files.add(ff);
        }
      }
    }
    
    message("Deleted "+counter+" empty folders");
  }
}
