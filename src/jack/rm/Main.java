package jack.rm;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jack.rm.assets.AssetPacker;
import jack.rm.assets.Downloader;
import jack.rm.data.console.GBA;
import jack.rm.data.console.System;
import jack.rm.data.rom.Language;
import jack.rm.data.rom.Location;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.Version;
import jack.rm.data.romset.*;
import jack.rm.files.GBASavePatcherGBATA;
import jack.rm.files.GBASleepHack;
import jack.rm.files.Scanner;
import jack.rm.files.UPSPatch;
import jack.rm.gui.*;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.workflow.*;

import com.pixbits.io.BinaryBuffer;
import com.pixbits.io.BinaryBuffer.Mode;
import com.pixbits.plugin.PluginManager;
import com.pixbits.workflow.*;

public class Main
{		
	public static PluginManager<ActualPlugin, ActualPluginBuilder> manager = new PluginManager<>(ActualPluginBuilder.class);

  public static MainFrame mainFrame;
	//public static InfoPanel infoPanel;
	
	public static ManagerPanel romsetPanel;
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
    
    manager.register(jack.rm.plugins.searcher.SimpleSearcherPlugin.class);
    manager.register(jack.rm.plugins.searcher.BaseSearchPredicates.class);
    
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
    
    /*List<Rom> favourites = set.filter("is:fav");
    Fetcher<RomHandle> source = new MultipleRomSource(favourites);
    Dumper<RomHandle> dumper = new RomConsolidator(Paths.get("/Users/jack/Documents/Dev/gba/ez/gb"));
    Workflow<RomHandle> workflow = new Workflow<>(source,dumper);
    workflow.addStep(new LogOperation());
    workflow.execute();
    java.lang.System.exit(0);*/

    /*List<Rom> favourites = set.filter("is:fav");*/
    
    /*List<Rom> favs = set.filter("is:fav");
    final Optional<Integer> c = Optional.of(0);
    Fetcher<RomHandle> source = new MultipleRomSource(favs);
    Dumper<RomHandle> dumper = new RomConsolidator(Paths.get("/Volumes/Vicky/nds"));
    Workflow<RomHandle> workflow = new Workflow<>(source, dumper);
    workflow.addStep(rh -> { java.lang.System.out.println(c.get()+" of "+favs.size()); return rh; });
    workflow.execute();*/
    
    //IPSPatchOperation ipsOperation = new IPSPatchOperation();
    //ipsOperation.toggleAutomaticPatching(true);
    //workflow.addBenchmarkedStep(new LogOperation());
    //workflow.addBenchmarkedStep(ipsOperation);
    //workflow.addBenchmarkedStep(new GBASleepHackOperation());
    //workflow.addBenchmarkedStep(new TrimOperation(new byte[] {0x00, (byte)0xff}));
    //workflow.addStep(new SortByAttributeOperation(RomAttribute.TAG, false));
    //workflow.execute();
    //java.lang.System.exit(0);
    
    
    /*
    try
    {
      BinaryBuffer buffer = new BinaryBuffer("/Users/jack/Documents/Dev/gba/sma-m.gba", BinaryBuffer.Mode.WRITE, ByteOrder.LITTLE_ENDIAN);
      java.lang.System.out.println(buffer.length());
      new GBASleepHack().patch(buffer);
      buffer.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    java.lang.System.exit(0);*/
	}
	
	/*public static void patchTest()
	{

	  
	  try
	  {
	    Stream<Path> ffiles = Files.list(Paths.get("/Users/jack/Desktop/save test/gbata")).filter(p -> p.getFileName().toString().endsWith(".gba"));
	    ffiles.forEach(f -> { 
	      try {
	        Files.move(f, f.getParent().resolve(
	          f.getFileName().toString().startsWith("l") ?
	          Paths.get("f"+f.getFileName()) :
	          Paths.get("e"+f.getFileName())
	        )); 
	      } 
	      catch (Exception e) 
	      { 
	        e.printStackTrace(); 
	      }
	    });
	    
	    if (true) return; 
	    
	    Stream<Path> files = Files.list(Paths.get("/Users/jack/Desktop/save test/original")).filter(p -> p.getFileName().toString().endsWith(".gba"));
	    Path dest = Paths.get("/Users/jack/Desktop/save test/rm");
	    files.forEach(p -> {
	      try
	      {
	        Path newFile = dest.resolve(p.getFileName());
	        Files.copy(p, newFile);
	        String filename = newFile.getFileName().toString();
	        
	        GBA.Save.Type type = null;
	        Version version = null;
	        
	        if (filename.contains("eeprom"))
	        {
	          type = GBA.Save.Type.EEPROM;
	          
	          for (Version v : GBA.Save.EEPROM.values())
	          {
	            if (filename.contains(v.toString().substring(1)))
	            {
	              version = v;
	              break;
	            }
	          }

	        }
	        else if (filename.contains("flash"))
	        {
	          type = GBA.Save.Type.FLASH;
	          
	           for (Version v : GBA.Save.Flash.values())
	            {
	              if (filename.contains(v.toString().substring(1)))
	              {
	                version = v;
	                break;
	              }
	            }
	        }
	        
	        GBA.Save save = new GBA.Save(type, version);
	        BinaryBuffer buffer = new BinaryBuffer(newFile, BinaryBuffer.Mode.WRITE, ByteOrder.LITTLE_ENDIAN);
	        GBASavePatcherGBATA.patch(save, buffer);
	        buffer.close();     
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	      }
	      
	    });
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
	}*/

	
	public static void main(String[] args)
	{
	  //patchTest();
	  
	  /*try {
	  UPSPatch patch = new UPSPatch(Paths.get("/Volumes/WinSSD/gba-ips/mother3.ups"));
	  BinaryBuffer buffer = new BinaryBuffer("/Volumes/WinSSD/gba-ips/mother3.gba", BinaryBuffer.Mode.WRITE, ByteOrder.LITTLE_ENDIAN);
	  patch.apply(buffer);
	  buffer.close();
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }*/
	  
	  if (true)
	  {
	  setOS();
	  setLNF();
	  
	  loadPlugins();
	  
	  RomSetManager.buildRomsetList();
	  
	  romsetPanel = new ManagerPanel();
	  pluginsPanel = new PluginsPanel(manager);
		
		mainFrame = new MainFrame();
    clonesDialog = new ClonesDialog(mainFrame, "Rom Clones");

		
    loadRomSet(RomSetManager.bySystem(System.GBA));

    //AssetPacker.packAssets(RomSet.current);

    mainFrame.pluginStateChanged();
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
