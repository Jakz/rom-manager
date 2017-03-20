package jack.rm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.ui.Icon;

class RomCellRenderer extends JPanel implements ListCellRenderer<Game>
{
	private static final long serialVersionUID = 1L;

	private final JLabel mainLabel = new JLabel();
	private final JLabel rightIcon = new JLabel();
	
	RomCellRenderer()
	{
	  setOpaque(true);

	  mainLabel.setFont(new Font("Default",Font.PLAIN,12));
	  
	  setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
	  setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	  
	  rightIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	  
	  add(mainLabel);
	  add(rightIcon);
	}
	
	@Override
  public Component getListCellRendererComponent(JList<? extends Game> list, Game rom, int index, boolean iss, boolean chf)
	{
	  mainLabel.setText(rom.toString());
	  Location location = rom.getAttribute(GameAttribute.LOCATION);
	  
	  if (location != null)
	    mainLabel.setIcon(location.icon.getIcon());
	  else
	    mainLabel.setIcon(null);
		
	  if (rom.isFavourite())
	    rightIcon.setIcon(Icon.FAVORITE.getIcon());
	  else
	    rightIcon.setIcon(null);
		
		if (iss)
		{
		  mainLabel.setForeground(Color.WHITE);
		  setBackground(rom.status.color);
		}
		else
		{
		  setBackground(list.getBackground());
		  mainLabel.setForeground(rom.status.color);
		}
		
		return this;
	}
}