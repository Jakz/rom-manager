package jack.rm.plugins.renamer;

import jack.rm.data.Rom;

public class BasicRenamerPlugin extends RenamerPlugin
{
  @Override public String getCorrectName(Rom rom)
  {
    return rom.title;
  }
  
  @Override public boolean isNative() { return true; }
}
