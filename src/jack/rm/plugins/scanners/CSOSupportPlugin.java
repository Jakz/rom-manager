package jack.rm.plugins.scanners;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.github.jakz.romlib.support.cso.CSOBinaryHandle;
import com.github.jakz.romlib.support.cso.CSOInfo;
import com.pixbits.lib.io.archive.handles.BinaryHandle;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.log.Log;

import jack.rm.log.LogSource;
import jack.rm.plugins.types.FormatSupportPlugin;

public class CSOSupportPlugin extends FormatSupportPlugin
{
  @Override
  public Handle getSpecializedEntry(Handle entry)
  {
    try
    {  
      if (entry instanceof BinaryHandle)
      {
        BinaryHandle handle = (BinaryHandle)entry;
        
        if (handle.getExtension().compareToIgnoreCase("cso") == 0)
        {
          CSOInfo info = new CSOInfo(handle.path());
          Log.getLogger(LogSource.SCANNER).i3("Found potential CSO iso: "+handle.path());
          return new CSOBinaryHandle(handle.path(), info);
        }
      }    
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return entry;
  }

}
