package jack.rm;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Desktop;
import java.util.function.Consumer;
import jack.rm.assets.AssetPacker;
import jack.rm.assets.Downloader;
import jack.rm.data.console.System;
import jack.rm.data.romset.*;
import jack.rm.files.Scanner;
import jack.rm.gui.*;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import com.pixbits.plugin.PluginManager;
import com.pixbits.workflow.*;

public class Main
{		
	public static PluginManager<ActualPlugin, ActualPluginBuilder> manager = new PluginManager<>(ActualPluginBuilder.class);

  public static MainFrame mainFrame;
	//public static InfoPanel infoPanel;
	
	public static ManagerPanel romsetPanel;
	public static PatternRenamerPanel renamerPanel;
	public static PluginsPanel pluginsPanel;
	
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
	
	static class IntHolder implements WorkflowData
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
	
	public static void loadPlugins()
	{
    manager.register(jack.rm.plugins.renamer.BasicRenamerPlugin.class);
    
    manager.register(jack.rm.plugins.folder.NumericalOrganizer.class);
    manager.register(jack.rm.plugins.folder.AlphabeticalOrganizer.class);
    manager.register(jack.rm.plugins.folder.RootOrganizer.class);
    manager.register(jack.rm.plugins.cleanup.DeleteEmptyFoldersPlugin.class);
    manager.register(jack.rm.plugins.cleanup.MoveUnknownFilesPlugin.class);
    manager.register(jack.rm.plugins.cleanup.ArchiveMergerPlugin.class);
    manager.register(jack.rm.plugins.renamer.BasicPatternSet.class);
    manager.register(jack.rm.plugins.renamer.NumberedRomPattern.class);
    manager.register(jack.rm.plugins.renamer.BasicRenamerPlugin.class);
    manager.register(jack.rm.plugins.renamer.PatternRenamerPlugin.class);
    manager.register(jack.rm.plugins.downloader.EmuParadiseDownloader.class);
    manager.register(jack.rm.plugins.providers.offlinelist.OfflineListProviderPlugin.class);
    manager.register(jack.rm.plugins.providers.clrmame.ClrMamePlugin.class);


	}
	
	public static void loadRomSet(RomSet romSet)
	{
	  if (RomSet.current != null)
	    RomSet.current.saveStatus();
	  
	  RomSet set = RomSetManager.loadSet(romSet);
    RomSet.current = set;
    boolean wasInit = set.loadStatus();

    mainFrame.romSetLoaded(set);
    
    scanner = new Scanner(set);
    scanner.scanForRoms(!wasInit);


    downloader = new Downloader(set);

    /*List<Rom> favourites = set.list.stream().filter(Searcher.buildSeachPredicate("is:fav")).collect(Collectors.toList());
    
    Fetcher<RomHandle> source = new MultipleRomSource(favourites);
    Dumper<RomHandle> dumper = new EZFlashIVRomConsolidator();
    Workflow<RomHandle> workflow = new Workflow<>(source, dumper);
    
    IPSPatchOperation ipsOperation = new IPSPatchOperation();
    ipsOperation.addPatch(romSet.list.find("yoshi universal loc:europe"), Paths.get("/Volumes/WinSSD/gba-ips/yoshi-universal-gravitation-tilt-fix.ips"));
    ipsOperation.addPatch(romSet.list.find("wario twisted"), Paths.get("/Volumes/WinSSD/gba-ips/wario-ware-tilt-fix.ips"));
    ipsOperation.addPatch(romSet.list.find("kuru paradise"), Paths.get("/Volumes/WinSSD/gba-ips/kururin-paradise-translation.ips"));
    
    workflow.addStep(new LogOperation());
    workflow.addStep(ipsOperation);
    workflow.addStep(new GBASavePatchOperationGBATA());
    workflow.execute();
    java.lang.System.exit(0);*/
	}
	
	public static void main(String[] args)
	{
	  if (true)
	  {
	  setOS();
	  setLNF();
	  
	  loadPlugins();
	  
	  RomSetManager.buildRomsetList();
	  
	  romsetPanel = new ManagerPanel();
	  renamerPanel = new PatternRenamerPanel();
	  pluginsPanel = new PluginsPanel(manager);
		
		mainFrame = new MainFrame();
    clonesDialog = new ClonesDialog(mainFrame, "Rom Clones");

		
    loadRomSet(RomSetManager.bySystem(System.GBA));
    
    AssetPacker.packAssets(RomSet.current);

		
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
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
