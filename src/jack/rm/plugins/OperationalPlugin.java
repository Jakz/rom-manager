package jack.rm.plugins;

import jack.rm.data.romset.GameList;

public interface OperationalPlugin
{
  public String getSubmenuCaption();
  public String getMenuCaption();
  public void execute(GameList list);
}
