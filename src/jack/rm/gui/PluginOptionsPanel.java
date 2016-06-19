package jack.rm.gui;

import javax.swing.*;

import jack.rm.data.romset.RomSet;

public abstract class PluginOptionsPanel extends JPanel
{
  public RomSet getRomset()
  {
    return RomSet.current;
  }
  
  public abstract String getTitle();
  public abstract void updateFields();
}
