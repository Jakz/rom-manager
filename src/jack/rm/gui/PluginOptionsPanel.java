package jack.rm.gui;

import javax.swing.JPanel;

import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.Main;

public abstract class PluginOptionsPanel extends JPanel
{
  public GameSet getGameSet()
  {
    return Main.current;
  }
  
  public abstract String getTitle();
  public abstract void updateFields();
}
