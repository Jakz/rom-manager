package jack.rm.gui.gameinfo;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.LanguageSet;

import com.github.jakz.romlib.data.game.attributes.Attribute;

class LanguageAttributeField extends AttributeField
{
  public static enum Mode
  {
    FLAG_ONLY,
    ISO639_ONLY,
    FULL_NAME_ONLY,
    FLAG_AND_ISO639,
    FLAG_AND_FULL_NAME
  }
  
  private JPanel panel;
  private Mode mode;
  
  LanguageAttributeField(InfoPanel infoPanel, Attribute attrib, boolean isReal)
  {
    super(infoPanel, attrib, isReal);    
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    
    mode = Mode.FLAG_AND_ISO639;

  }

  void enableEdit()
  {

  }
  
  void finishEdit()
  {

  }

  void setValue(Game game)
  {
    LanguageSet set = game.getAttribute(attrib);
    panel.removeAll();
    
    for (Language language : set)
    {
      JLabel label = new JLabel();
      
      if (mode == Mode.FLAG_ONLY || mode == Mode.FLAG_AND_ISO639 || mode == Mode.FLAG_AND_FULL_NAME)
        label.setIcon(language.icon.getIcon());
      
      if (mode == Mode.ISO639_ONLY || mode == Mode.FLAG_AND_ISO639)
        label.setText(language.iso639_1);
      else if (mode == Mode.FULL_NAME_ONLY || mode == Mode.FLAG_AND_FULL_NAME)
        label.setText(language.fullName);
      
      label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      
      if (isReal)
        label.setFont(panel.getFont().deriveFont(Font.BOLD, 12.0f)); 
      
      panel.add(label);
    }
    
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