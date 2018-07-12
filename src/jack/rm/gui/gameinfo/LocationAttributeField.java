package jack.rm.gui.gameinfo;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.LocationSet;
import com.github.jakz.romlib.data.game.attributes.Attribute;

class LocationAttributeField extends AttributeField
{
  private JPanel panel;
  
  LocationAttributeField(InfoPanel infoPanel, Attribute attrib, boolean isReal)
  {
    super(infoPanel, attrib, isReal);    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
  }

  void enableEdit()
  {

  }
  
  void finishEdit()
  {

  }

  void setValue(Game game)
  {
    LocationSet location = game.getAttribute(attrib);
    panel.removeAll();
       
    JLabel label = new JLabel();
    
    label.setIcon(location.getIcon().getIcon());
    label.setText(location.toString());

    if (isReal)
      label.setFont(panel.getFont().deriveFont(Font.BOLD, 12.0f)); 
    
    panel.add(label);

    panel.revalidate();
  }
  
  void clear()
  {
    panel.removeAll();
    panel.revalidate();
  }

  @Override
  Object parseValue()
  {
    return null;
  }

  @Override
  JComponent getComponent()
  {
    return panel;
  }

  @Override
  void attributeCleared()
  {
    panel.removeAll();
    panel.revalidate();   
  }
}