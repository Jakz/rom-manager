package jack.rm.plugins;

import com.github.jakz.romlib.data.set.GameSet;

public interface OperationalPlugin
{
  public String getSubmenuCaption();
  public String getMenuCaption();
  public void execute(GameSet set);
}
