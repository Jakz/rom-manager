package jack.rm.data.set;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.assets.Asset;
import jack.rm.assets.AssetManager;
import jack.rm.data.*;
import jack.rm.data.console.System;
import jack.rm.data.parser.DatLoader;
import jack.rm.data.rom.RomAttribute;
import jack.rm.json.Json;
import jack.rm.json.RomListAdapter;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.cleanup.CleanupPlugin;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RomSet
{
  public static RomSet current = null;
	
  private boolean loaded;

	public final RomList list;
	public final System system;
	public final Provider provider;
	public final ProviderType providerType;
	
	private Settings settings;
	private final AssetManager assetManager;
	private final DatLoader loader;
	
	
	private final RomAttribute[] attributes;

	public RomSet(System type, Provider provider, ProviderType providerType, RomAttribute[] attributes, AssetManager assetManager, DatLoader loader)
	{
		this.list = new RomList(this);
	  this.system = type;
		this.provider = provider;
		this.providerType = providerType;
		this.attributes = attributes;
		this.assetManager = assetManager;
		this.loader = loader;
		this.loaded = false;
	}
	
	public Settings getSettings() { return settings; }
	
	public final AssetManager getAssetManager() { return assetManager; }
	
	public boolean doesSupportAttribute(RomAttribute attribute) { return Arrays.stream(attributes).anyMatch( a -> a == RomAttribute.NUMBER); }
	public final RomAttribute[] getSupportedAttributes() { return attributes; }
			
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
		return provider.getTag()+"-"+system.tag+"-"+providerType.getIdent();
	}
	
	public String datPath()
	{
		return "dat/"+ident()+".xml";
	}

	public PathMatcher getFileMatcher()
	{
	  Stream<String> stream = Arrays.stream(system.exts);
	  
	  if (system.acceptsArchives)
	    stream = Stream.concat(stream, Arrays.stream(new String[]{"zip"}));
	  
	  String pattern = stream.collect(Collectors.joining(",", "glob:*.{", "}"));
	  	  
	  return FileSystems.getDefault().getPathMatcher(pattern);
	}
	
  public final Path getAssetPath(Asset asset, Rom rom)
  {
    Path path = Paths.get("assets",ident()).resolve(asset.getPath());  
    return rom == null ? path : path.resolve(rom.getAssetData(asset).getPath());
  }
	
	public final void cleanup()
	{
	  Set<CleanupPlugin> plugins = settings.plugins.getEnabledPlugins(PluginRealType.ROMSET_CLEANUP);
	  plugins.stream().forEach( p -> p.execute(this.list) );
	}
	
	public void saveStatus()
	{
	  try
	  {
  	  Path basePath = Paths.get("data/", ident());
  	  
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
        Log.message(LogSource.STATUS, LogTarget.romset(this), "Romset status saved on json");
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
  	      Files.createDirectories(getAssetPath(asset, null));
  	  }
  	  catch (IOException e)
  	  {
  	    e.printStackTrace();
  	    // TODO: log
  	  }
  	  
  	  if (!Files.exists(settingsPath))
  	  {
  	    settings = new Settings(Main.manager);
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
  	        Log.log(LogType.ERROR, LogSource.PLUGINS, null, "Error while loading plugin state: "+e.getCause().toString());
  	      
  	      e.printStackTrace();
  	    }
  	    
  	    Path statusPath = basePath.resolve("status.json");
  	    
  	    Gson gson = Json.prebuild().registerTypeAdapter(RomList.class, new RomListAdapter(list)).create();
  	    
  	    try (BufferedReader rdr = Files.newBufferedReader(statusPath))
  	    {
  	      gson.fromJson(rdr, RomList.class);
  	    }
  	    
  	    return true;
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
