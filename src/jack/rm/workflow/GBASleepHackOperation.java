package jack.rm.workflow;

import com.github.jakz.romlib.data.platforms.Platform;

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
  
  protected RomWorkflowEntry doApply(RomWorkflowEntry handle)
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
  
  public boolean isPlatformSupported(Platform platform)
  {
    return platform == Platform.GBA;
  }
  
}
