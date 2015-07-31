package jack.rm.workflow;

import jack.rm.data.console.System;
import jack.rm.data.rom.RomAttribute;
import jack.rm.files.GBASavePatcherGBATA;
import jack.rm.files.GBASleepHack;

public class GBASleepHackOperation extends RomOperation
{
  public String getName() { return "GBA Sleephack"; }
  public String getDescription() { return "This operation patches a GBA rom to enable sleep and reset for EZ-IV flash, credits to dwedit/kuwanger"; }
  
  private GBASleepHack hacker;
  
  public GBASleepHackOperation()
  {
    hacker = new GBASleepHack();
  }
  
  protected RomHandle doApply(RomHandle handle)
  {
    try
    {
      hacker.patch(handle.getBuffer());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return handle;
  }
  
  public boolean isSystemSupported(System system)
  {
    return system == System.GBA;
  }
  
}
