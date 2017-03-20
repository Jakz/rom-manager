package jack.rm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;

import jack.rm.data.rom.Rom;

class CellRenderer extends JLabel implements ListCellRenderer<Rom>
{
	private static final long serialVersionUID = 1L;

	CellRenderer()
	{
		setOpaque(true);
	}
	
	@Override
  public Component getListCellRendererComponent(JList<? extends Rom> list, Rom rom, int index, boolean iss, boolean chf)
	{
		setFont(new Font("Default",Font.PLAIN,12));
		setText(rom.toString());
		
		Location location = rom.getAttribute(GameAttribute.LOCATION);
		setIcon(location.icon.getIcon());
		
		setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		
		if (iss)
		{
			setForeground(Color.WHITE);
			setBackground(rom.status.color);
			
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(rom.status.color);
		}
		
		return this;
	}
}