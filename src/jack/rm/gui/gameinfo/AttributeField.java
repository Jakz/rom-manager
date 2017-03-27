package jack.rm.gui.gameinfo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.ui.Icon;

import jack.rm.Main;

abstract class AttributeField
{
  /**
   * 
   */
  protected final InfoPanel infoPanel;
  protected JLabel title;
  protected Attribute attrib;

  protected JButton deleteButton;
  protected JButton moveUpButton;
  protected JButton moveDownButton;

  abstract Object parseValue();
  
  abstract void attributeCleared();
  
  abstract JComponent getComponent();
  
  AttributeField(InfoPanel infoPanel, Attribute attrib, boolean isReal)
  {
    this.infoPanel = infoPanel;
    this.attrib = attrib;
    title = new JLabel();
    title.setHorizontalAlignment(SwingConstants.RIGHT);
    title.setText(attrib.getCaption());

    deleteButton = new JButton();
    deleteButton.setIcon(Icon.DELETE.getIcon());
    deleteButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    deleteButton.setVisible(false);
    deleteButton.addActionListener( e -> {
      infoPanel.rom.clearCustomAttribute(attrib);
      setValue(infoPanel.rom);
      attributeCleared();
    });
    
    moveUpButton = new JButton();
    moveUpButton.setIcon(Icon.ARROW_UP.getIcon());
    moveUpButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    moveUpButton.setVisible(false);
    
    moveDownButton = new JButton();
    moveDownButton.setIcon(Icon.ARROW_DOWN.getIcon());
    moveDownButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    moveDownButton.setVisible(false);
  }
  
  void clearCustomAttribute()
  {
    if (this.infoPanel.rom.hasCustomAttribute(attrib))
    {
      infoPanel.rom.clearCustomAttribute(attrib);
      setValue(this.infoPanel.rom);
      infoPanel.rom.updateStatus();
      Main.mainFrame.refreshGameListCurrentSelection();
    }
    deleteButton.setVisible(false);
  }

  abstract void enableEdit();
  abstract void finishEdit();
  

  
  abstract void setValue(Game rom);
  abstract void clear();
}