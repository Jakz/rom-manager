package jack.rm;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Desktop;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jack.rm.data.*;
import jack.rm.data.console.System;
import jack.rm.data.set.*;
import jack.rm.gui.*;
import jack.rm.net.Downloader;
import jack.rm.plugins.ActualPlugin;

import com.pixbits.workflow.*;
import com.pixbits.workflow.base.*;

public class Main
{		
	public static MainFrame mainFrame;
	//public static InfoPanel infoPanel;
	
	public static ManagerPanel romsetPanel;
	public static PatternRenamerPanel renamerPanel;
	public static PluginsPanel pluginsPanel;
	public static OptionsFrame optionsFrame;
	
	public static ClonesDialog clonesDialog;
	
	
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
	
	static class IntHolder implements Data
	{
	  public int value;
	  
	  IntHolder(int value) { this.value = value; }
	  
	  int get() { return value; }
	}
	
	static class IntFetcher extends Fetcher<IntHolder>
	{
	  int size = 20;
	  int counter = 0;
	  
	  IntFetcher()
	  {
	    super(20);
	  }
	  
	  @Override public boolean tryAdvance(Consumer<? super IntHolder> action)
	  {
	    if (counter < size)
	    {
	      action.accept(new IntHolder(counter));
	      ++counter;
	      return true;
	    }
	    return false;
	  } 
	}
	
	static class IntDumper extends Dumper<IntHolder>
	{
	  @Override public void accept(IntHolder holder) { /*System.out.println(holder.get());*/ }
	}
	
	public static void main(String[] args)
	{
	  /*IntFetcher fetcher = new IntFetcher();
	  IntDumper dumper = new IntDumper();
	  Workflow<IntHolder> workflow = new Workflow<>(fetcher, dumper);
	  workflow.addStep( i -> {i.value *= 2; return i; } );
	  workflow.addStep( i -> {i.value += 17; return i; } );
	  workflow.execute();*/
	  
	  if (true)
	  {
	  setOS();
	  setLNF();
		
		mainFrame = new MainFrame();
		
		romsetPanel = new ManagerPanel();
		renamerPanel = new PatternRenamerPanel();
		pluginsPanel = new PluginsPanel(ActualPlugin.manager);
		
		optionsFrame = new OptionsFrame();
		
    RomSetManager.loadSet(System.GBA);
		scanner = new Scanner(RomSet.current.list);
    scanner.scanForRoms(!RomSet.current.list.load());

		downloader = new Downloader(RomSet.current);

		
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
    clonesDialog = new ClonesDialog(mainFrame, "Rom Clones");
		
	
		Settings.consolidate();
	  }
	}

	enum OS
	{
	  WIN,
	  OSX,
	  LINUX
	}
	
	private static void setOS()
	{
	  String system = java.lang.System.getProperty("os.name").toLowerCase();
	  
	  if (system.indexOf("win") >= 0)
	    os = OS.WIN;
	  else if (system.indexOf("mac") >= 0)
	    os = OS.OSX;
	  else
	    os = OS.OSX;
	}
	
	private static OS os;
	public static void openFolder(java.io.File folder)
	{
	  try {
	    Desktop.getDesktop().open(folder);
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
}
