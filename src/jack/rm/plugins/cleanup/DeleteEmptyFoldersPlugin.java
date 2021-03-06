package jack.rm.plugins.cleanup;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.plugins.types.CleanupPlugin;

public class DeleteEmptyFoldersPlugin extends CleanupPlugin
{
  @Override public void execute(GameSet set)
  {
    Queue<File> files = new LinkedList<File>();
    
    MyGameSetFeatures helper = set.helper();
    files.add(helper.settings().romsPath.toFile());
    
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
  
  @Override public String getSubmenuCaption() { return "Cleanup"; }
  @Override public String getMenuCaption() { return "Delete empty folders"; }
}
