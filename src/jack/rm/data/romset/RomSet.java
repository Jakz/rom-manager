package jack.rm.data.romset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.pixbits.lib.log.Log;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.assets.Asset;
import jack.rm.assets.AssetManager;
import jack.rm.data.console.System;
import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.search.DummySearcher;
import jack.rm.data.search.Searcher;
import jack.rm.files.parser.DatLoader;
import jack.rm.json.Json;
import jack.rm.json.RomListAdapter;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.cleanup.CleanupPlugin;

public class RomSet
{
  public static RomSet current = null;
	
  private boolean loaded;

	public final RomList list;
	public final System system;
	public final Provider provider;
	public final DatFormat datFormat;
	
	private Settings settings;
	private final AssetManager assetManager;
	private final DatLoader loader;
	
	private Searcher searcher;
	
	
	private final Attribute[] attributes;

	public RomSet(System type, Provider provider, Attribute[] attributes, AssetManager assetManager, DatLoader loader)
	{
		this.searcher = new DummySearcher(this);
	  this.list = new RomList(this);
	  this.system = type;
		this.provider = provider;
		this.datFormat = loader.getFormat();
		this.attributes = attributes;
		this.assetManager = assetManager;
		this.loader = loader;
		this.loaded = false;
	}
	
	public void pluginStateChanged()
	{
	  if (getSettings().getSearchPlugin() != null)
	    searcher = new Searcher(this);
	  else
	    searcher = new DummySearcher(this);
	}
	
	public Settings getSettings() { return settings; }
	
	public final AssetManager getAssetManager() { return assetManager; }
	
	public boolean doesSupportAttribute(Attribute attribute) { return Arrays.stream(attributes).anyMatch( a -> a == RomAttribute.NUMBER); }
	public final Attribute[] getSupportedAttributes() { return attributes; }
			
	public boolean canBeLoaded()
	{
	  return Files.exists(datPath());
	}
	
	public final void load()
	{ 
	  if (!loaded)
	    loader.load(this); 
	  loaded = true;
	}
	
	@Override
  public String toString()
	{
		return system.name+" ("+provider.getName()+")";
	}
	
	public String ident()
	{
		return datFormat.getIdent()+"-"+system.tag+"-"+provider.getTag()+provider.builtSuffix();
	}
	
	public Path datPath()
	{
		return Paths.get("dat/"+ident()+"."+datFormat.getExtension());
	}
	
	public Path getAttachmentPath()
	{
	  return settings.romsPath.resolve(Paths.get("attachments"));
	}
	
	public Searcher getSearcher()
	{
	  return searcher;
	}
	
  public final Path getAssetPath(Asset asset, boolean asArchive)
  {
    Path base = Paths.get("data/", ident(), "assets").resolve(asset.getPath());
    
    if (!asArchive)
      return base;
    else
      return Paths.get(base.toString()+".zip");
  }
	
	public final void cleanup()
	{
	  Set<CleanupPlugin> plugins = settings.plugins.getEnabledPlugins(PluginRealType.ROMSET_CLEANUP);
	  plugins.stream().forEach( p -> p.execute(this.list) );
	}
	
	public Rom find(String query) { return list.find(query); }
	public List<Rom> filter(String query) { return list.stream().filter(searcher.search(query)).collect(Collectors.toList()); }
	
	public void saveStatus()
	{
	  try
	  {
  	  Path basePath = GlobalSettings.DATA_PATH.resolve(ident());
  	  
  	  Files.createDirectories(basePath);
  	  
  	  Path settingsPath = basePath.resolve("settings.json");
  	  
  	  try (BufferedWriter wrt = Files.newBufferedWriter(settingsPath))
  	  {
  	    wrt.write(Json.build().toJson(settings, Settings.class));
  	  }
  	  
  	  Path statusPath = basePath.resolve("status.json");
  	  
      Gson gson = Json.prebuild().registerTypeAdapter(RomList.class, new RomListAdapter(list)).create();
      
      try (BufferedWriter wrt = Files.newBufferedWriter(statusPath))
      {
        wrt.write(gson.toJson(list));
        Log.getLogger(LogSource.STATUS).i(LogTarget.romset(this), "Romset status saved on json");
      }
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
	}
		
	public boolean loadStatus()
	{
	  try
	  {
  	  Path basePath = Paths.get("data/", ident());
  	    
  	  Path settingsPath = basePath.resolve("settings.json");
  	  
  	  try
  	  {
  	    AssetManager assetManager = getAssetManager();
  	    for (Asset asset : assetManager.getSupportedAssets())
  	      Files.createDirectories(getAssetPath(asset,false));
  	  }
  	  catch (IOException e)
  	  {
  	    e.printStackTrace();
  	    // TODO: log
  	  }
  	  
  	  if (!Files.exists(settingsPath))
  	  {
  	    settings = new Settings(Main.manager, Arrays.asList(getSupportedAttributes()));
  	    return false;
  	  }
  	  else
  	  {
  	    try (BufferedReader rdr = Files.newBufferedReader(settingsPath))
  	    {
  	      settings = Json.build().fromJson(rdr, Settings.class);
  	    }
  	    catch (JsonParseException e)
  	    {
  	      if (e.getCause() instanceof ClassNotFoundException)
  	        Log.getLogger(LogSource.STATUS).e("Error while loading plugin state: %s", e.getCause().toString());
  	      
  	      e.printStackTrace();
  	    }
  	    
  	    Path statusPath = basePath.resolve("status.json");
  	    
  	    Gson gson = Json.prebuild().registerTypeAdapter(RomList.class, new RomListAdapter(list)).create();
  	    
  	    try (BufferedReader rdr = Files.newBufferedReader(statusPath))
  	    {
  	      gson.fromJson(rdr, RomList.class);
  	      return true;
  	    }
  	    catch (NoSuchFileException e)
  	    {
  	      return false;
  	    }
  	  }
	  }
	  catch (FileNotFoundException e)
	  {
	    return false;
	  }
	  catch (IOException e)
	  {
	    e.printStackTrace();
	    return false;
	  }
	}
}
