package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.i18n.Text;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;

public class OptionsFrame extends JFrame implements ActionListener, ComponentListener
{
	private static final long serialVersionUID = 1L;
	
	private GameSet set;

	JTabbedPane tabs = new JTabbedPane();
	
	JButton close = new JButton(Text.TEXT_CLOSE.text());

	private final PluginManager<ActualPlugin, ActualPluginBuilder> manager;
	private final GameSetManager setManager;
		
	public OptionsFrame(PluginManager<ActualPlugin, ActualPluginBuilder> manager, GameSetManager setManager)
	{
		setTitle(Text.MENU_TOOLS_OPTIONS.text());
		
		this.manager = manager;
		this.setManager = setManager;
		
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
		
    tabs.addTab(Text.OPTION_ROMSET.text(), Main.romsetPanel);
    tabs.addTab(Text.OPTION_PLUGINS.text(), Main.pluginsPanel);
		
		Main.romsetPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    Main.pluginsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lower.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		setPreferredSize(new Dimension(600,600));
		pack();
	}
	
	public void romSetLoaded(GameSet set)
	{
	  this.set = set;
	  rebuildGUIComponents();
	}
	
	public void showMe()
	{
		if (this.isVisible())
			return;		  
		
		Main.romsetPanel.updateFields(set);

		for (int i = 2; i < tabs.getTabCount(); ++i)
		  ((PluginOptionsPanel)tabs.getComponentAt(i)).updateFields();
		
		Main.pluginsPanel.populate(set);
		setLocationRelativeTo(Main.mainFrame);
		
		setVisible(true);
	}
	
	public void rebuildGUIComponents()
	{
	  int tabCount = tabs.getTabCount();
	  for (int i = 2; i < tabCount; ++i)
	    tabs.removeTabAt(2);
	  
	  MyGameSetFeatures helper = set.helper();

    manager.stream()
    .map(b -> helper.settings().getPlugin(b.getID()))
    .filter(p -> p.isPresent() && p.get().isEnabled())
    .forEach(p -> {
      PluginOptionsPanel panel = p.get().getGUIPanel();
      if (panel != null)
      {
        tabs.addTab(panel.getTitle(), panel);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.updateFields();
      }
    });
	}
	
	public void pluginStateChanged()
	{
	  rebuildGUIComponents();
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
	  set.checkNames();
    setManager.saveSetStatus(set);
    Main.mainFrame.rebuildGameList();
	}
}
