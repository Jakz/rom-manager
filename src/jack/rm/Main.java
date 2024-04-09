package jack.rm;

import java.awt.Desktop;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.jakz.romlib.data.assets.Downloader;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.LogBuffer;
import com.pixbits.lib.plugin.PluginManager;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.UIUtils.OperatingSystem;
import com.pixbits.lib.ui.elements.ProgressDialog;
import com.pixbits.lib.workflow.Dumper;
import com.pixbits.lib.workflow.Fetcher;
import com.pixbits.lib.workflow.Mutuator;
import com.pixbits.lib.workflow.Workflow;
import com.pixbits.lib.workflow.WorkflowData;

import jack.rm.data.romset.GameSetManager;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.files.MyAssetDownloader;
import jack.rm.gui.ClonesDialog;
import jack.rm.gui.Dialogs;
import jack.rm.gui.GlobalSettingsView;
import jack.rm.gui.MainFrame;
import jack.rm.gui.SetInfoPanel;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.gui.PluginsPanel;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.workflow.LogOperation;
import jack.rm.workflow.MultipleGameSource;
import jack.rm.workflow.organizers.OrganizeByAttribute;
import jack.rm.workflow.organizers.RenameByExportTitle;
import jack.rm.workflow.GameConsolidator;
import jack.rm.workflow.GameEntry;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;

public class Main
{		
	public static final PluginManager<ActualPlugin, ActualPluginBuilder> manager = new PluginManager<>(ActualPluginBuilder.class);
	
	//TODO: should be private
	public static GameSetManager setManager = new GameSetManager(manager);
	public static GameSet current = null;
	
	public static ProgressDialog.Manager progress;

  public static MainFrame mainFrame;
	//public static InfoPanel infoPanel;
	
  public static GlobalSettingsView gsettingsView;
	public static SetInfoPanel romsetPanel;
	public static PluginsPanel pluginsPanel;
	
	public static ClonesDialog clonesDialog;

	public static Downloader downloader;

	public static void initZipLibrary()
	{
	  try
    {
      SevenZip.initSevenZipFromPlatformJAR();
    } 
	  catch (SevenZipNativeInitializationException e)
    {
      e.printStackTrace();
    }
	}
	
	public static LogBuffer logBuffer;
	public static void initLogging()
	{
	  /*logBuffer = new LogBuffer();
	  logBuffer.setCallback(b -> {
	    if (mainFrame != null)
	      mainFrame.logPanel.populate();
	  });
	  
	  LoggerFactory factory = new LoggerFactory.BufferLoggerFactory(logBuffer);
	  Log.setFactory(factory);*/
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
    
    manager.register(jack.rm.plugins.fetchers.MobyGamesFetcher.class);
    
    //manager.register(jack.rm.plugins.providers.OfflineListProviderPlugin.class);
    //manager.register(jack.rm.plugins.providers.ClrMamePlugin.class);
    manager.register(jack.rm.plugins.providers.DatGuesserPlugin.class);

    manager.register(jack.rm.plugins.datparsers.ClrMameParserPlugin.class);
    manager.register(jack.rm.plugins.datparsers.OfflineListParserPlugin.class);
    manager.register(jack.rm.plugins.datparsers.LogiqxXmlParserPlugin.class);

    manager.register(jack.rm.plugins.misc.ExportRomsPlugin.class);

    manager.register(jack.rm.plugins.scanners.EmbeddedScanner.class);
    
    manager.register(jack.rm.plugins.scanners.DigestVerifier.class);
    
    manager.register(jack.rm.plugins.scanners.CSOSupportPlugin.class);
    manager.register(jack.rm.plugins.scanners.NesHeaderSupportPlugin.class);

	}
	
	public static void dumpInfo()
	{
	  
	}
	
	public static void loadRomSet(GameSet romSet) throws FileNotFoundException, IOException
	{ 
	  if (current != null)
	    setManager.saveSetStatus(current);
	  
	  GameSet set = setManager.loadSet(romSet);
    current = set;
    boolean wasInit = setManager.loadSetStatus(set);
    
    MyGameSetFeatures helper = set.helper();
    
    helper.pluginStateChanged();
    mainFrame.romSetLoaded(set);
    MyGameSetFeatures features = set.helper();
    features.organizer().computeStatus();
    
    helper.scanner().scanForRoms(!wasInit && GlobalSettings.settings.shouldScanWhenLoadingRomset());
    downloader = new MyAssetDownloader(set);

    /*List<Game> favourites = set.filter("is:fav");
    Fetcher<GameEntry> source = new MultipleGameSource(favourites);
    Mutuator<GameEntry> sorter = new OrganizeByAttribute(GameAttribute.TAG, false);
    Mutuator<GameEntry> renamer = new RenameByExportTitle();
    Dumper<GameEntry> dumper = new GameConsolidator(Paths.get("/Users/jack/Desktop/everdrive"));
    Workflow<GameEntry> workflow = new Workflow<>(source,dumper);
    workflow.addStep(new LogOperation());
    workflow.addStep(sorter);
    workflow.addStep(renamer);
    workflow.addStep(e -> {
      Game g = e.getGame();
      if (g.hasAnyCustomAttribute() && !g.isFavourite())
        System.out.println("Missing favorite on "+g.getTitle());
      return e;
    });
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
	
	public static void generateUniverse()
	{
	  List<GameSet> sets = new ArrayList<>();
	  	  
	  /* load all loadable sets */
	  for (GameSet set : setManager.allSets())
	  {
	    if (set.canBeLoaded())
	    {
	      set.load();
	      sets.add(set);
	    }
	  }
	  
    Log.getLogger(LogSource.STATUS).i(LogTarget.none(), "Loaded %d sets", sets.size()); 

	  
	  /* split all games by platform */
	  Map<Platform, List<Game>> gamesByPlatform = sets.stream().flatMap(GameSet::stream).collect(Collectors.groupingBy(Game::getPlatform));
	  
    Log.getLogger(LogSource.STATUS).i(LogTarget.none(), "Loaded %d games in %d platforms", gamesByPlatform.values().stream().map(List::size).mapToLong(i -> i).sum(), gamesByPlatform.size());  
	}

	
	public static void main(String[] args)
	{    
	  /*try
    {
	    Path path = Paths.get("/Volumes/Vicky/Roms/roms/psp/SORTED/Action/Diner Dash [EUR].cso");
	    
	    CSOBinaryHandle handle = new CSOBinaryHandle(path);
	    handle.computeCRC();
	    System.out.printf("%08X %s %s\n", handle.crc(), StringUtils.humanReadableByteCount(handle.compressedSize()), StringUtils.humanReadableByteCount(handle.size()));
	    
	    
	    CSOInfo info = new CSOInfo(path);
      System.out.println("Sectors: "+info.sectorCount()+" size: "+info.uncompressedSize());
      
      CSOInputStream stream = new CSOInputStream(path, info);
      MonitoredInputStream mis = new MonitoredInputStream(stream);
      mis.addChangeListener(e -> System.out.println(mis.location()));
        
      Digester digester = new Digester(new DigestOptions(true, false, false, false));
      DigestInfo d = digester.digest(null, mis);
        
      System.out.println(d);      
    } 
	  catch (IOException | NoSuchAlgorithmException e1)
    {
      e1.printStackTrace();
    }
	  
	  if (true)
	    return;*/
	  
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
	    Log.setLevel(Log.DEBUG, true);
	    initZipLibrary();
	    initLogging();
	    os = UIUtils.getOperatingSystem();
	    //UIUtils.setNimbusLNF();
	    //FlatLightLaf.setup(new com.formdev.flatlaf.themes.FlatMacDarkLaf());
	    FlatLightLaf.setup();
	    //UIUtils.setUIFont(new javax.swing.plaf.FontUIResource("Segoe UI", Font.PLAIN, 12));

	  
	    GlobalSettings.load();
	    loadPlugins();
	 
	    setManager.buildRomsetList();
	    
	    /*
	    generateUniverse();
	    if (true)
	      return;
	    */
	    
	    GlobalSettings.settings.sanitize(setManager);
	  
	    romsetPanel = new SetInfoPanel();
	    pluginsPanel = new PluginsPanel(manager, setManager);
		  gsettingsView = new GlobalSettingsView(setManager);
		  mainFrame = new MainFrame(setManager);
		
      clonesDialog = new ClonesDialog(setManager, mainFrame, "Rom Clones");
    
      progress = new ProgressDialog.Manager(mainFrame);

      String lastProvider = GlobalSettings.settings.getCurrentProvider();
 
    if (lastProvider != null)
    {
      try
      {
        loadRomSet(setManager.byIdent(lastProvider));
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
	
	private static OperatingSystem os;
	public static void openFolder(java.io.File folder)
	{
	  try {
	    Desktop.getDesktop().open(folder);
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}
}
