package jack.rm.plugins;

import jack.rm.data.romset.RomList;

public interface OperationalPlugin
{
  public String getSubmenuCaption();
  public String getMenuCaption();
  public void execute(RomList list);
}
