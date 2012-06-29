package jack.rm.gui;

import jack.rm.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class MenuListener implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if (source == Main.mainFrame.miRoms[0])
		{
			Main.scanner.scanForRoms();
			Main.mainFrame.updateTable();
		}
		else if (source == Main.mainFrame.miRoms[1])
		{
			//export missing				
		}
		else if (source == Main.mainFrame.miRoms[2])
		{
			//export correct
		}
		else if (source == Main.mainFrame.miRoms[3])
		{
			//Main.romList.saveStatus();
			System.exit(0);
		}
		else if (source == Main.mainFrame.miRoms[4])
		{
			Main.romList.renameRoms();
			
			if (Main.pref.organizeRomsByNumber)
			{
				Main.romList.organizeRomsByNumber();
				
				if (Main.pref.organizeRomsDeleteEmptyFolders)
					Main.romList.deleteEmptyFolders();
			}
			Main.mainFrame.updateTable();
		}
		else if (source == Main.mainFrame.miView[0])
		{
			Main.mainFrame.romListModel.isCorrect = !Main.mainFrame.romListModel.isCorrect;
			Main.mainFrame.romListModel.clear();
			Main.romList.showAll();
		}
		else if (source == Main.mainFrame.miView[1])
		{
			Main.mainFrame.romListModel.isMissing = !Main.mainFrame.romListModel.isMissing;
			Main.mainFrame.romListModel.clear();
			Main.romList.showAll();
		}
		else if (source == Main.mainFrame.miView[2])
		{
			Main.mainFrame.romListModel.isBadlyNamed = !Main.mainFrame.romListModel.isBadlyNamed;
			Main.mainFrame.romListModel.clear();
			Main.romList.showAll();
		}
		else if (source == Main.mainFrame.miTools[0])
		{
			Main.downloader.start();
		}
		else if (source == Main.mainFrame.miTools[1])
		{
			Main.renamerFrame.showMe();
		}
		else if (source == Main.mainFrame.miTools[2])
		{
			Main.mainFrame.toggleConsole(Main.mainFrame.miTools[2].isSelected());
		}
		else
		{
			//StringManager.loadLanguage(((JMenuItem)source).getText());
		}
	}
}