package jack.rm.gui;

import javax.swing.JPanel;

import com.github.jakz.romlib.data.set.GameSet;

public abstract class PluginOptionsPanel extends JPanel
{
  public GameSet getRomset()
  {
    return GameSet.current;
  }
  
  public abstract String getTitle();
  public abstract void updateFields();
}
