package jack.rm.gui;

import jack.rm.data.rom.Location;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;

import javax.swing.*;
import java.awt.*;

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
		
		Location location = rom.getAttribute(RomAttribute.LOCATION);
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