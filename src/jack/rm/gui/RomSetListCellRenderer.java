package jack.rm.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import jack.rm.data.romset.GameSet;

final class RomSetListCellRenderer extends DefaultListCellRenderer
{
  @Override
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
  {
    JLabel c = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    GameSet set = (GameSet)value;   
    if (set != null)
      c.setIcon(set.platform.getIcon());
    return c;
  }
}