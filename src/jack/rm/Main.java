package jack.rm;

import java.awt.Desktop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.LogBuffer;
import com.pixbits.lib.log.LoggerFactory;
import com.pixbits.lib.plugin.PluginManager;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.elements.ProgressDialog;
import com.pixbits.workflow.Dumper;
import com.pixbits.workflow.Fetcher;
import com.pixbits.workflow.WorkflowData;

import jack.rm.assets.Downloader;
import jack.rm.data.romset.GameSet;
import jack.rm.data.romset.GameSetManager;
import jack.rm.files.Scanner;
import jack.rm.gui.ClonesDialog;
import jack.rm.gui.Dialogs;
import jack.rm.gui.GlobalSettingsView;
import jack.rm.gui.MainFrame;
import jack.rm.gui.ManagerPanel;
import jack.rm.gui.PluginsPanel;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;

public class Main
{		
	public static PluginManager<ActualPlugin, ActualPluginBuilder> manager = new PluginManager<>(ActualPluginBuilder.class);
	
	public static ProgressDialog.Manager progress = new ProgressDialog.Manager();

  public static MainFrame mainFrame;
	//public static InfoPanel infoPanel;
	
  public static GlobalSettingsView gsettingsView;
	public static ManagerPanel romsetPanel;
	public static PluginsPanel pluginsPanel;
	
	public static ClonesDialog clonesDialog;
	
	
	public static Downloader downloader;
		
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

	
	public static LogBuffer logBuffer;
	public static void initLogging()
	{
	  logBuffer = new LogBuffer();
	  logBuffer.setCallback(b -> {
	    if (mainFrame != null)
	      mainFrame.logPanel.populate();
	  });
	  
	  LoggerFactory factory = new LoggerFactory.BufferLoggerFactory(logBuffer);
	  Log.setFactory(factory);
	}
	
	public static void loadPlugins()
	{
    manager.register(jack.rm.plugins.renamer.BasicRenamerPlugin.class);
    
    manager.register(jack.rm.plugins.searcher.SimpleSearcherPlugin.class);
    manager.register(jack.rm.plugins.searcher.BooleanSearcherPlugin.class);
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
    
    manager.register(jack.rm.plugins.providers.OfflineListProviderPlugin.class);
    manager.register(jack.rm.plugins.providers.ClrMamePlugin.class);
    
    manager.register(jack.rm.plugins.datparsers.ClrMameParserPlugin.class);
    manager.register(jack.rm.plugins.datparsers.OfflineListParserPlugin.class);

    manager.register(jack.rm.plugins.misc.ExportRomsPlugin.class);

    manager.register(jack.rm.plugins.scanners.EmbeddedScanner.class);
    
    manager.register(jack.rm.plugins.scanners.DigestVerifier.class);

	}
	
	public static void loadRomSet(GameSet romSet) throws FileNotFoundException, IOException
	{
	  if (GameSet.current != null)
	    GameSet.current.saveStatus();
	  
	  GameSet set = GameSetManager.loadSet(romSet);
    GameSet.current = set;
    boolean wasInit = set.loadStatus();
    
    GameSet.current.pluginStateChanged();


    mainFrame.romSetLoaded(set);
    
    GameSet.current.getScanner().scanForRoms(!wasInit && GlobalSettings.settings.shouldScanWhenLoadingRomset());

    downloader = new Downloader(set);
    
    /*List<Rom> zip7 = set.filter("format:7z");
    Fetcher<RomHandle> source = new SingleRomSource(zip7.get(0));
    Dumper<RomHandle> dumper = new RomConsolidator(Paths.get("/Users/jack/Desktop/CAH"));
    Workflow<RomHandle> workflow = new Workflow<>(source,dumper);
    workflow.addStep(new LogOperation());
    workflow.execute();
    java.lang.System.exit(0);*/
    
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
	  
	  initLogging();
	  setOS();
	  UIUtils.setNimbusLNF();
	  
	  GlobalSettings.load();
	  loadPlugins();
	  
	  GameSetManager.buildRomsetList();
	  
	  romsetPanel = new ManagerPanel();
	  pluginsPanel = new PluginsPanel(manager);
		gsettingsView = new GlobalSettingsView();
	  
		mainFrame = new MainFrame();
    clonesDialog = new ClonesDialog(mainFrame, "Rom Clones");

    String lastProvider = GlobalSettings.settings.getCurrentProvider();
 
    if (lastProvider != null)
    {
      try
      {
        loadRomSet(GameSetManager.byIdent(lastProvider));
        mainFrame.pluginStateChanged();
      }
      catch (FileNotFoundException e)
      {
        Dialogs.showError("DAT File not found!", "Missing DAT file for set "+lastProvider, Main.mainFrame);
      } 
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
  	/*	try
  		{
  		  URL test = new URL("http://www.advanscene.com/offline/datas/ADVANsCEne_GBA.zip");
  		  
  		  //URL test = new URL("http://ubunturel.mirror.garr.it/mirrors/ubuntu-releases/16.04/ubuntu-16.04-desktop-amd64.iso");
  		  Path savePath = Paths.get("/Users/jack/Desktop/CAH/gba.zip");
  		  Path extractPath = Paths.get("/Users/jack/Desktop/CAH/gba.xml");
  		  
        Consumer<Boolean> uncompressStep = a -> {
          ZipExtractWorker<?> worker =  new ZipExtractWorker<BackgroundOperation>(savePath, extractPath, new BackgroundOperation() {
            public String getTitle() { return "Uncompressing"; }
            public String getProgressText() { return "Progress.."; }
          }, r -> {}, mainFrame);
          worker.execute();
        };
  		
    		DownloadWorker<?> worker = new DownloadWorker<BackgroundOperation>(test, savePath, new BackgroundOperation() {
          public String getTitle() { return "Downloading"; }
          public String getProgressText() { return "Progress.."; }
        }, uncompressStep, mainFrame);
    		
    		worker.execute();
    		

  	  }
  		catch (Exception e)
  		{
  		  e.printStackTrace();
  		}*/
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
