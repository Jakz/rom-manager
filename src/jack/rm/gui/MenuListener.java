package jack.rm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;

import jack.rm.Main;
import jack.rm.data.romset.GameList;
import jack.rm.data.romset.GameSet;

class MenuListener implements ActionListener
{
	public static final MenuListener listener = new MenuListener();
  
  @Override
  public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		JMenuItem item = (JMenuItem)source;
		MenuElement tag = MenuElement.elementForItem(item);
		
		try
		{
  		if (tag == MenuElement.ROMS_SCAN_FOR_ROMS)
  		{
  		  GameSet.current.getScanner().scanForRoms(true);
  			Main.mainFrame.updateTable();
  		}
  		else if (tag == MenuElement.ROMS_SCAN_FOR_NEW_ROMS)
  		{
  		  GameSet.current.getScanner().scanForRoms(false);
  			Main.mainFrame.updateTable();
  		}
  		else if (tag == MenuElement.VIEW_SHOW_CORRECT)
  		{
  			Main.mainFrame.romListModel.isCorrect = !Main.mainFrame.romListModel.isCorrect;
  			Main.mainFrame.updateTable();
  		}
  		else if (tag == MenuElement.VIEW_SHOW_NOT_FOUND)
  		{
  			Main.mainFrame.romListModel.isMissing = !Main.mainFrame.romListModel.isMissing;
        Main.mainFrame.updateTable();
  		}
  		else if (tag == MenuElement.VIEW_SHOW_UNORGANIZED)
  		{
  			Main.mainFrame.romListModel.isBadlyNamed = !Main.mainFrame.romListModel.isBadlyNamed;
        Main.mainFrame.updateTable();
  		}
  		else
  		{
  			//StringManager.loadLanguage(((JMenuItem)source).getText());
  		}
		}
		catch (IOException ee)
		{
		  ee.printStackTrace();
		}
	}
}