package jack.rm;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;

import jack.rm.data.parser.*;
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
		        //UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("Helvetica", Font.PLAIN, 14));
		        break;
		      }
		  }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
	}
	
	public static void main(String[] args)
	{
	  setOS();
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
		
		mainFrame.setLocationRelativeTo(null);
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
	
	enum OS
	{
	  WIN,
	  OSX,
	  LINUX
	}
	
	private static void setOS()
	{
	  String system = System.getProperty("os.name").toLowerCase();
	  
	  if (system.indexOf("win") >= 0)
	    os = OS.WIN;
	  else if (system.indexOf("mac") >= 0)
	    os = OS.OSX;
	  else
	    os = OS.OSX;
	}
	
	private static OS os;
	public static void openFolder(File folder)
	{
	  try {
	    Desktop.getDesktop().open(folder);
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
}
