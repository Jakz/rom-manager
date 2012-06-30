package jack.rm.gui;

import jack.rm.Main;
import jack.rm.i18n.Text;

import javax.swing.*;
import java.awt.*;

public class OptionsFrame extends JFrame
{
	JTabbedPane tabs = new JTabbedPane();
	
	
	public OptionsFrame()
	{
		setTitle(Text.MENU_TOOLS_OPTIONS.text());
		
		tabs.addTab(Text.OPTION_RENAMER.text(), Main.renamerPanel);
		
		
		this.add(tabs);
		
		
		pack();
	}
	
	public void showMe()
	{
		if (this.isVisible())
			return;
		
		Main.renamerPanel.updateFields();
		setLocationRelativeTo(Main.mainFrame);
		
		setVisible(true);
	}
}
