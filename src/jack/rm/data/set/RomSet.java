package jack.rm.data.set;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.*;
import jack.rm.data.console.System;
import jack.rm.json.Json;
import jack.rm.json.RomListAdapter;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;
import jack.rm.net.AssetManager;
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

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class RomSet<R extends Rom>
{
  public static RomSet<? extends Rom> current = null;
	
	public final RomList list;
	public final System type;
	public final ProviderID provider;
	
	public final Dimension screenTitle;
	public final Dimension screenGame;
	
	private Settings settings;
	private final AssetManager assetManager;

	RomSet(System type, ProviderID provider, Dimension screenTitle, Dimension screenGame, AssetManager assetManager)
	{
		this.list = new RomList(this);
	  this.type = type;
		this.provider = provider;
		this.screenTitle = screenTitle;
		this.screenGame = screenGame;
		this.assetManager = assetManager;
	}
	
	public Settings getSettings() { return settings; }
	
	public final AssetManager getAssetManager() { return assetManager; }
			
	public abstract void load();
	

	@Override
  public String toString()
	{
		return type.name+" ("+provider.name+")";
	}
	
	public String ident()
	{
		return provider.tag+"-"+type.tag;
	}
	
	public String datPath()
	{
		return "dat/"+ident()+".xml";
	}

	public boolean hasGameArt()
	{
		return screenGame != null;
	}
	
	public boolean hasTitleArt()
	{
		return screenTitle != null;
	}
	
	public Path romPath()
	{
		return settings.romsPath;
	}
	
	public PathMatcher getFileMatcher()
	{
	  Stream<String> stream = Arrays.stream(type.exts);
	  
	  if (type.acceptsArchives)
	    stream = Stream.concat(stream, Arrays.stream(new String[]{"zip"}));
	  
	  String pattern = stream.collect(Collectors.joining(",", "glob:*.{", "}"));
	  	  
	  return FileSystems.getDefault().getPathMatcher(pattern);
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
	
  public final Path getAssetPath(Asset asset, Rom rom)
  {
    Path path = Paths.get("assets",ident()).resolve(asset == Asset.SCREEN_GAMEPLAY ? "game/" : "title/");  
    return rom == null ? path : path.resolve(getAssetManager().assetPath(asset, rom));
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
