package jack.rm.gui;

import jack.rm.Main;

import java.awt.*;
import javax.swing.*;

public class CountPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	JLabel[] counters = new JLabel[4];
	String[] icons = new String[]{"status_correct","status_badly_named","status_not_found","status_all"};
	
	public CountPanel()
	{
		JPanel inner = new JPanel();
		for (int i = 0; i < counters.length; ++i)
		{
			counters[i] = new JLabel("1234");
			counters[i].setIcon(new ImageIcon("data/images/"+icons[i]+".png"));
			counters[i].setPreferredSize(new Dimension(55,12));
			inner.add(counters[i]);
		}
		
		this.setLayout(new BorderLayout());
		this.add(inner, BorderLayout.WEST);
	}
	
	public void update()
	{	  
	  counters[0].setText(""+Main.romList.countCorrect);
		counters[1].setText(""+Main.romList.countBadlyNamed);
		counters[2].setText(""+Main.romList.countNotFound);
		counters[3].setText(""+Main.romList.countTotal);
	}
}
