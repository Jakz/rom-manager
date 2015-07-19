package jack.rm.gui;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.set.RomSet;
import jack.rm.i18n.Text;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class OptionsFrame extends JFrame implements ActionListener, ComponentListener
{
	private static final long serialVersionUID = 1L;

	JTabbedPane tabs = new JTabbedPane();
	
	JButton close = new JButton(Text.TEXT_CLOSE.text());
		
	public OptionsFrame()
	{
		setTitle(Text.MENU_TOOLS_OPTIONS.text());
		
		tabs.addTab(Text.OPTION_ROMSET.text(), Main.romsetPanel);
		tabs.addTab(Text.OPTION_RENAMER.text(), Main.renamerPanel);
		tabs.addTab(Text.OPTION_PLUGINS.text(), Main.pluginsPanel);
	
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout());
		all.add(tabs, BorderLayout.CENTER);
		
		JPanel lower = new JPanel();
		lower.setLayout(new GridLayout(1,5));
		
		for (int i = 0; i < 5; ++i)
		{
			if (i != 5 / 2)
				lower.add(new JLabel());
			else
				lower.add(close);
		}
		
		all.add(lower, BorderLayout.SOUTH);
		close.addActionListener(this);
		
		this.addComponentListener(this);
		
		this.add(all);
		
		Main.romsetPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		Main.renamerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    Main.pluginsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lower.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		setPreferredSize(new Dimension(600,600));
		pack();
	}
	
	public void showMe()
	{
		if (this.isVisible())
			return;		  
		
		Main.romsetPanel.updateFields();
		Main.renamerPanel.updateFields();
		Main.pluginsPanel.populate(RomSet.current);
		setLocationRelativeTo(Main.mainFrame);
		
		setVisible(true);
	}
	
	@Override
  public void actionPerformed(ActionEvent e)
	{
		this.setVisible(false);
	}

	@Override
  public void componentMoved(ComponentEvent e) { }
	@Override
  public void componentResized(ComponentEvent e) { }
	@Override
  public void componentShown(ComponentEvent e) { }
	
	@Override
  public void componentHidden(ComponentEvent e)
	{
	  RomSet.current.list.checkNames();
	  Settings.consolidate();
	}
}
