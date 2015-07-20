package jack.rm.gui;

import jack.rm.*;
import jack.rm.data.RomList;
import jack.rm.data.set.RomSet;
import java.awt.event.*;
import javax.swing.*;

class MenuListener implements ActionListener
{
	public static final MenuListener listener = new MenuListener();
  
  @Override
  public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		JMenuItem item = (JMenuItem)source;
		MenuElement tag = MenuElement.elementForItem(item);
		
		RomList list = RomSet.current.list;

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
		else if (tag == MenuElement.ROMS_CLEANUP)
		{
		  RomSet.current.cleanup();
		}
		else if (tag == MenuElement.ROMS_EXIT)
		{
			//Main.romList.saveStatus();
			System.exit(0);
		}
		else if (tag == MenuElement.ROMS_RENAME)
		{
		  list.renameRoms();
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
		else if (tag == MenuElement.TOOLS_DOWNLOAD_ART)
		{
			Main.downloader.start();
		}
		else
		{
			//StringManager.loadLanguage(((JMenuItem)source).getText());
		}
	}
}