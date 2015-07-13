package jack.rm.gui;

import jack.rm.Main;
import jack.rm.data.RomList;
import jack.rm.data.set.RomSet;

import java.awt.*;
import javax.swing.*;

public class CountPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	JLabel[] counters = new JLabel[4];
	Icon[] icons = new Icon[]{Icon.STATUS_CORRECT, Icon.STATUS_BADLY_NAMED, Icon.STATUS_NOT_FOUND, Icon.STATUS_ALL};
	
	public CountPanel()
	{
		JPanel inner = new JPanel();
		for (int i = 0; i < counters.length; ++i)
		{
			counters[i] = new JLabel("1234");
			counters[i].setIcon(icons[i].getIcon());
			counters[i].setPreferredSize(new Dimension(55,12));
			inner.add(counters[i]);
		}
		
		this.setLayout(new BorderLayout());
		this.add(inner, BorderLayout.WEST);
	}
	
	public void update()
	{	  
	  RomList list = RomSet.current.list;
    counters[0].setText(""+list.getCountCorrect());
    counters[1].setText(""+list.getCountBadName());
    counters[2].setText(""+list.getCountMissing());
    counters[3].setText(""+list.count());
	}
}
