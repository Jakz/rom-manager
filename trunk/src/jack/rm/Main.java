package jack.rm;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jack.rm.data.*;
import jack.rm.data.parser.*;
import jack.rm.gui.*;

public class Main
{
	public static final Preferences pref = new Preferences();
	
	public static final RomList romList = new RomList();
	
	public static MainFrame mainFrame;
	public static SearchPanel searchPanel;
	public static InfoPanel infoPanel;
	public static CountPanel countPanel;
	
	public static RenamerFrame renamerFrame;
	
	
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
		DatLoader.load();
		
		searchPanel = new SearchPanel();
		infoPanel = new InfoPanel();
		countPanel = new CountPanel();
		
		mainFrame = new MainFrame();
		
		//ProgressDialog dg = new ProgressDialog(mainFrame,"Progress");
		//dg.setVisible(true);
		
		renamerFrame = new RenamerFrame();
		
		scanner = new Scanner(romList);
		scanner.scanForRoms();
		
		downloader = new Downloader();

		romList.showAll();
		
		mainFrame.setVisible(true);
		
		
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
