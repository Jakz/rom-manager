package jack.rm.plugins.cleanup;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import jack.rm.data.RomList;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;

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
    
    Log.log(LogType.MESSAGE, LogSource.PLUGINS, LogTarget.plugin(this), "Deleted "+counter+" empty folders");
  }
}
