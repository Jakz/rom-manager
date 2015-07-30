package jack.rm.plugins.renamer;

import jack.rm.data.rom.Rom;

public class BasicRenamerPlugin extends RenamerPlugin
{
  @Override public String getCorrectName(Rom rom)
  {
    return rom.getTitle();
  }
  
  @Override public boolean isNative() { return true; }
}
