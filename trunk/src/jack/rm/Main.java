package jack.rm;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jack.rm.data.*;
import jack.rm.data.set.*;
import jack.rm.gui.*;

public class Main
{
	public static final Preferences pref = new Preferences();
	
	public static final RomList romList = new RomList();
	
	public static MainFrame mainFrame;
	public static SearchPanel searchPanel;
	public static InfoPanel infoPanel;
	public static CountPanel countPanel;
	
	public static ManagerPanel romsetPanel;
	public static RenamerPanel renamerPanel;
	public static OptionsFrame optionsFrame;
	
	
	public static Scanner scanner;
	public static Downloader downloader;
	
	
	public static void setLNF()
	{
		try {
		  for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		      if ("Nimbus".equals(info.getName())) {
		        UIManager.setLookAndFeel(info.getClassName());
		          break;
		      }
		  }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
	}
	
	public static void main(String[] args)
	{
		setLNF();
		
		searchPanel = new SearchPanel();
		infoPanel = new InfoPanel();
		countPanel = new CountPanel();
		mainFrame = new MainFrame();
		
		romsetPanel = new ManagerPanel();
		renamerPanel = new RenamerPanel();
		
		optionsFrame = new OptionsFrame();
		
		scanner = new Scanner(romList);
		downloader = new Downloader();

		RomSetManager.loadSet(Console.GBA);
		
		mainFrame.setVisible(true);
	
		Settings.consolidate();
	}
	
	public static void log(String str)
	{
		mainFrame.cardConsole.append(str);
	}
	
	public static void logln(String str)
	{
		mainFrame.cardConsole.appendln(str);
	}
}
