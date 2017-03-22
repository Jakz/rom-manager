package jack.rm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.LocationSet;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.ui.Icon;

class CellRenderer extends JLabel implements ListCellRenderer<Game>
{
	private static final long serialVersionUID = 1L;

	CellRenderer()
	{
		setOpaque(true);
	}
	
	@Override
  public Component getListCellRendererComponent(JList<? extends Game> list, Game game, int index, boolean iss, boolean chf)
	{
		setFont(new Font("Default",Font.PLAIN,12));
		setText(game.toString());
		
		LocationSet location = game.getLocation();
		Icon icon = location.getIcon();
		setIcon(icon != null ? icon.getIcon() : null);
		
		setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		
		if (iss)
		{
			setForeground(Color.WHITE);
			setBackground(game.status.color);
			
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(game.status.color);
		}
		
		return this;
	}
}