package jack.rm.gui;

import jack.rm.data.*;
import javax.swing.*;
import java.awt.*;

class RomCellRenderer extends JPanel implements ListCellRenderer<Rom>
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
	  
	  add(mainLabel);
	  add(rightIcon);
	}
	
	@Override
  public Component getListCellRendererComponent(JList<? extends Rom> list, Rom rom, int index, boolean iss, boolean chf)
	{
	  mainLabel.setText(rom.toString());
	  mainLabel.setIcon(rom.location.icon.getIcon());
		
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