package jack.rm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JMenuItem;

import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;

class MenuListener implements ActionListener
{
	public static final MenuListener listener = new MenuListener();
  
  @Override
  public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		JMenuItem item = (JMenuItem)source;
		MenuElement tag = MenuElement.elementForItem(item);
		
		MyGameSetFeatures helper = Main.current.helper();
		
		try
		{
  		if (tag == MenuElement.ROMS_SCAN_FOR_ROMS)
  		{
  		  helper.scanner().scanForRoms(true);
  			Main.mainFrame.rebuildGameList();
  		}
  		else if (tag == MenuElement.ROMS_SCAN_FOR_NEW_ROMS)
  		{
  		  helper.scanner().scanForRoms(false);
      Main.mainFrame.rebuildGameList();
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