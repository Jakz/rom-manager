package jack.rm.gui;

import jack.rm.*;
import jack.rm.files.Organizer;

import java.awt.event.*;
import javax.swing.*;

class MenuListener implements ActionListener
{
	public static final MenuListener listener = new MenuListener();
  
  public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		JMenuItem item = (JMenuItem)source;
		MenuElement tag = MenuElement.elementForItem(item);

		if (tag == MenuElement.ROMS_SCAN_FOR_ROMS)
		{
			Main.scanner.scanForRoms(true);
			Main.mainFrame.updateTable();
		}
		else if (tag == MenuElement.ROMS_SCAN_FOR_NEW_ROMS)
		{
			Main.scanner.scanForRoms(false);
			Main.mainFrame.updateTable();
		}
		else if (tag == MenuElement.ROMS_EXPORT_MISSING)
		{
			//export missing				
		}
		else if (tag == MenuElement.ROMS_EXPORT_FOUND)
		{
			//export correct
		}
		else if (tag == MenuElement.ROMS_EXIT)
		{
			//Main.romList.saveStatus();
			System.exit(0);
		}
		else if (tag == MenuElement.ROMS_RENAME)
		{
			Main.romList.renameRoms();
			Main.mainFrame.updateTable();
		}
		else if (tag == MenuElement.VIEW_SHOW_CORRECT)
		{
			Main.mainFrame.romListModel.isCorrect = !Main.mainFrame.romListModel.isCorrect;
			Main.mainFrame.romListModel.clear();
			Main.romList.showAll();
		}
		else if (tag == MenuElement.VIEW_SHOW_NOT_FOUND)
		{
			Main.mainFrame.romListModel.isMissing = !Main.mainFrame.romListModel.isMissing;
			Main.mainFrame.romListModel.clear();
			Main.romList.showAll();
		}
		else if (tag == MenuElement.VIEW_SHOW_BADLY_NAMED)
		{
			Main.mainFrame.romListModel.isBadlyNamed = !Main.mainFrame.romListModel.isBadlyNamed;
			Main.mainFrame.romListModel.clear();
			Main.romList.showAll();
		}
		else if (tag == MenuElement.TOOLS_DOWNLOAD_ART)
		{
			Main.downloader.start();
		}
    else if (tag == MenuElement.TOOLS_MOVE_UNKNOWN_FILES)
    {
      Organizer.moveUnknownFiles(Main.romList);
    }
		else if (tag == MenuElement.TOOLS_OPTIONS)
		{
			Main.optionsFrame.showMe();
		}
		else if (tag == MenuElement.TOOLS_SHOW_CONSOLE)
		{
			Main.mainFrame.toggleConsole(item.isSelected());
		}
		else
		{
			//StringManager.loadLanguage(((JMenuItem)source).getText());
		}
	}
}