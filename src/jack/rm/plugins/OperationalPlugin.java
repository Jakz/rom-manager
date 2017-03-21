package jack.rm.plugins;

import jack.rm.data.romset.GameSet;

public interface OperationalPlugin
{
  public String getSubmenuCaption();
  public String getMenuCaption();
  public void execute(GameSet set);
}
