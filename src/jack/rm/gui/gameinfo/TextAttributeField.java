package jack.rm.gui.gameinfo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.game.attributes.RomAttribute;
import com.pixbits.lib.io.archive.handles.Handle;

import jack.rm.Main;
import jack.rm.gui.gameinfo.InfoPanel.Mode;

class TextAttributeField extends AttributeField implements CaretListener, ActionListener
{
  private JTextField value;
  
  private Border defaultBorder;
  private Color defaultColor;
    
  JComponent getComponent() { return value; }
  
  Object parseValue()
  {
     if (attrib.getClazz() == String.class)
        return value.getText();
      else if (attrib.getClazz() == Integer.class)
      {
        try {
          return Integer.parseInt(value.getText());
        } catch (Exception e) { return null; }
      }
      
      return null;
  }
  
  TextAttributeField(InfoPanel infoPanel, Attribute attrib, boolean isReal)
  {
    super(infoPanel, attrib, isReal);    
    value = new JTextField(40);
    
    if (isReal)
      value.setFont(value.getFont().deriveFont(Font.BOLD, 12.0f)); 

    defaultBorder = value.getBorder();
    Insets insets = defaultBorder.getBorderInsets(value);
    value.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
    value.setEditable(false);
    Color color = UIManager.getColor("Panel.background");
    defaultColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
    value.setBackground(defaultColor);
  }
  
  void attributeCleared()
  {
    value.setBackground(Color.WHITE);
  }
  
  void enableEdit()
  {
    if (attrib.getClazz() != null)
    {
      value.setEditable(true);
      value.setBorder(defaultBorder);
      value.setBackground(Color.WHITE);     
      value.addCaretListener(this);
      value.addActionListener(this);
      
      deleteButton.setVisible(this.infoPanel.game.hasCustomAttribute(attrib));
    }
  }
  
  void finishEdit()
  {
    if (attrib.getClazz() != null)
    {
      Insets insets = defaultBorder.getBorderInsets(value);
      value.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
      value.setEditable(false);
      value.setBackground(defaultColor);
      value.removeCaretListener(this);
      value.removeActionListener(this);
    
      deleteButton.setVisible(false);
    }
  }

  public void caretUpdate(CaretEvent e)
  {
    Object pv = parseValue();
     
    if (pv != null && !pv.equals(this.infoPanel.game.getAttribute(attrib)))
      value.setBackground(new Color(255,175,0));
    else
      value.setBackground(Color.WHITE);
  }
   
  public void actionPerformed(ActionEvent e)
  {
    Object pv = parseValue();
     
    if (pv != null && !pv.equals(this.infoPanel.game.getAttribute(attrib)))
    {
      this.infoPanel.game.setCustomAttribute(attrib, pv);
      value.setBackground(Color.WHITE);
      deleteButton.setVisible(true);
      infoPanel.game.updateStatus();
      Main.mainFrame.refreshGameListCurrentSelection();
    }
    else
      setValue(this.infoPanel.game);
  }
  
  void setValue(Game game)
  {
    deleteButton.setVisible(this.infoPanel.mode == Mode.EDIT && game.hasCustomAttribute(attrib));

    if (attrib == GameAttribute.PATH)
    {
      Handle handle = game.rom().handle();
      String handleValue = handle == null ? "" : handle.toString();
      value.setText(handleValue);     
    }
    else if (attrib instanceof RomAttribute)
    {
      Rom rom = game.rom();
      value.setText(attrib.prettyValue(rom.getAttribute((RomAttribute)attrib)));
    }
    else
      value.setText(attrib.prettyValue(game.getAttribute(attrib)));

  }
  
  void clear()
  {
    value.setText("");
  }
}