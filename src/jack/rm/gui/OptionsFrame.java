package jack.rm.gui;

import jack.rm.Main;
import jack.rm.data.romset.RomSet;
import jack.rm.i18n.Text;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;

import java.awt.event.*;
import java.util.Optional;

import javax.swing.*;

import com.pixbits.plugin.PluginManager;

import java.awt.*;

public class OptionsFrame extends JFrame implements ActionListener, ComponentListener
{
	private static final long serialVersionUID = 1L;
	
	private RomSet set;

	JTabbedPane tabs = new JTabbedPane();
	
	JButton close = new JButton(Text.TEXT_CLOSE.text());

	private final PluginManager<ActualPlugin, ActualPluginBuilder> manager;
		
	public OptionsFrame(PluginManager<ActualPlugin, ActualPluginBuilder> manager)
	{
		setTitle(Text.MENU_TOOLS_OPTIONS.text());
		
		this.manager = manager;
		
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
    Main.pluginsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lower.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		setPreferredSize(new Dimension(600,600));
		pack();
	}
	
	public void romSetLoaded(RomSet set)
	{
	  this.set = set;
	  
	  tabs.removeAll();
	  tabs.addTab(Text.OPTION_ROMSET.text(), Main.romsetPanel);
	  tabs.addTab(Text.OPTION_PLUGINS.text(), Main.pluginsPanel);
	  
    manager.stream()
    .map(b -> set.getSettings().plugins.getPlugin(b.getID()))
    .filter(p -> p.isPresent() && p.get().isEnabled())
    .forEach(p -> {
      PluginOptionsPanel panel = p.get().getGUIPanel();
      if (panel != null)
      {
        tabs.addTab(panel.getTitle(), panel);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      }
    });
	}
	
	public void showMe()
	{
		if (this.isVisible())
			return;		  
		
		Main.romsetPanel.updateFields();
		
		for (int i = 2; i < tabs.getTabCount()-2; ++i)
		  ((PluginOptionsPanel)tabs.getTabComponentAt(i)).updateFields();
		
		Main.pluginsPanel.populate(set);
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
	  set.list.checkNames();
	  set.saveStatus();
	}
}
