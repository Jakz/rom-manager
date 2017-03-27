package jack.rm.gui.gameinfo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.Attribute;

import jack.rm.Main;
import jack.rm.gui.gameinfo.InfoPanel.Mode;

class EnumAttributeField extends AttributeField
{
  /**
   * 
   */
  private JComboBox<Enum<?>> value;
  private JTextField readValue;
  private JPanel panel;
  
  //@SuppressWarnings("unchecked")
  EnumAttributeField(InfoPanel infoPanel, Attribute attrib, boolean isReal)
  {
    super(infoPanel, attrib, isReal);
    value = new JComboBox<Enum<?>>();
    readValue = new JTextField(40);
    
    Color color = UIManager.getColor("Panel.background");
    Color tmpColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
    readValue.setBackground(tmpColor);
    readValue.setEditable(false);
    Insets insets = readValue.getBorder().getBorderInsets(readValue);
    readValue.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
    
    value.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED)
      {
        this.infoPanel.rom.setCustomAttribute(attrib, value.getSelectedItem());
        deleteButton.setVisible(true);
        this.infoPanel.rom.updateStatus();
        readValue.setText(value.getSelectedItem().toString());
        Main.mainFrame.refreshGameListCurrentSelection();
      }
    });
    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    panel.add(readValue);
    
    if (isReal)
    {
      value.setFont(value.getFont().deriveFont(Font.BOLD, 14.0f));
      readValue.setFont(value.getFont().deriveFont(Font.BOLD, 14.0f)); 
    }
    
    try
    {
      Enum<?>[] values = (Enum<?>[])attrib.getClazz().getMethod("values").invoke(null);
      Arrays.sort(values, (o1, o2) -> o1.toString().compareTo(o2.toString()));
      value.addItem(null);
      for (Enum<?> v : values)
        value.addItem(v);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
  }
  
  public JComponent getComponent() { return panel; }
  
  public void clear() { value.setSelectedIndex(-1); }
  
  public void attributeCleared()
  {
    
  }
  
  public void enableEdit()
  {
    panel.remove(readValue);
    panel.add(value);
    panel.revalidate();
    deleteButton.setVisible(this.infoPanel.rom.hasCustomAttribute(attrib));
  }
  
  public void finishEdit()
  {
    panel.remove(value);
    panel.add(readValue);
    panel.revalidate();
    deleteButton.setVisible(false);
  }
  
  public void setValue(Game rom)
  {
    
    Object ovalue = rom.getAttribute(attrib);
    value.setSelectedItem(ovalue);
    if (ovalue != null)
    {
      deleteButton.setVisible(this.infoPanel.mode == Mode.EDIT && rom.hasCustomAttribute(attrib));
      readValue.setText(ovalue.toString());
    }	    
    else
    {
      deleteButton.setVisible(false);
      readValue.setText("");
    }
  }
  
  public Object parseValue()
  {
    return value.getSelectedItem();
  }
}