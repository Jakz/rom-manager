package jack.rm.gui;

import jack.rm.data.*;
import javax.swing.*;
import java.awt.*;

class CellRenderer extends JLabel implements ListCellRenderer
{
	private static final long serialVersionUID = 1L;

	CellRenderer()
	{
		setOpaque(true);
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean iss, boolean chf)
	{
		Rom rom = (Rom)value;
		setFont(new Font("Default",Font.PLAIN,12));
		setText(rom.toString());
		
		setIcon(rom.location.icon);
		
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