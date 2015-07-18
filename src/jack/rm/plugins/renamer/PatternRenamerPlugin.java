package jack.rm.plugins.renamer;

import jack.rm.data.Rom;

public class PatternRenamerPlugin extends RenamerPlugin
{
  @Override public String getCorrectName(Rom rom)
  {
    return rom.title;
  }
}
