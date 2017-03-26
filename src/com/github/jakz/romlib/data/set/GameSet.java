package com.github.jakz.romlib.data.set;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.pixbits.lib.searcher.DummySearcher;
import com.pixbits.lib.searcher.SearchParser;
import com.pixbits.lib.searcher.SearchPredicate;
import com.pixbits.lib.searcher.Searcher;
import com.pixbits.lib.io.digest.HashCache;
import com.pixbits.lib.log.Log;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.assets.Asset;
import jack.rm.assets.AssetManager;
import jack.rm.files.Scanner;
import jack.rm.json.Json;
import jack.rm.json.GameListAdapter;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.searcher.SearchPlugin;
import jack.rm.plugins.searcher.SearchPredicatesPlugin;

public class GameSet implements Iterable<Game>
{
  public static GameSet current = null;
	
  private boolean loaded;

  public final Platform platform;
  private final GameSetInfo info;
  public final RomSize.Set sizeSet;

	private GameList list;
	private CloneSet clones;
	
	private Settings settings;
	
	private Searcher<Game> searcher;
	private Scanner scanner;

	private final Attribute[] attributes;

	public GameSet(Platform platform, Provider provider, DatLoader loader, Attribute[] attributes, AssetManager assetManager)
	{
		this.info = new GameSetInfo(provider, loader, assetManager);
	  this.searcher = new DummySearcher<>();
	  this.list = null;
	  this.clones = null;
	  this.sizeSet = new RomSize.Set();
	  this.platform = platform;
		this.attributes = attributes;
		this.loaded = false;
	}
	
	public GameSet(Platform platform, Provider provider, DatLoader loader)
  {
	  this.info = new GameSetInfo(provider, loader);
	  this.searcher = new DummySearcher<>();
	  this.list = null;
	  this.clones = null;
	  this.sizeSet = new RomSize.Set();
	  this.platform = platform;
	  this.attributes = new Attribute[0];
	  this.loaded = false;
  }
	
	public void pluginStateChanged()
	{
	  if (getSettings().getSearchPlugin() != null)
	  {
	    List<SearchPredicate<Game>> predicates = new ArrayList<>();
	    
	    SearchPlugin plugin = getSettings().plugins.getEnabledPlugin(PluginRealType.SEARCH);
	    SearchParser<Game> parser = plugin.getSearcher();
	    
	    Set<SearchPredicatesPlugin> predicatePlugins = getSettings().plugins.getEnabledPlugins(PluginRealType.SEARCH_PREDICATES);
	    predicatePlugins.stream().flatMap(p -> p.getPredicates().stream()).forEach(predicates::add);    
	    searcher = new Searcher<>(parser, predicates);
	  }
	  else
	    searcher = new DummySearcher<>();
	  
	  scanner = new Scanner(this);
	}
	
	public void setClones(CloneSet clones)
	{ 
    this.clones = clones;
    
    for (GameClone clone : clones)
      for (Game game : clone)
        game.setClone(clone);
	}
	
	public CloneSet clones() { return clones; }
	public GameSetInfo info() { return info; }
	public GameSetStatus status() { return list.status(); }
	public HashCache<Rom> hashCache() { return list.cache(); }
	public boolean hasMultipleRomsPerGame() { return list.hasMultipleRomsPerGame(); }
	
	public void checkNames() { list.checkNames(); }
	public void resetStatus() { list.resetStatus(); }
	public void refreshStatus() { list.refreshStatus(); }
	
	public Game getAny() { return get(0); }
	public Game get(int index) { return list.get(index); }
	public Game get(String title) { return list.get(title); }
	public int gameCount() { return list.gameCount(); }
	public Stream<Game> stream() { return list.stream(); }
	public Stream<Rom> romStream() { return list.stream().flatMap(g -> g.stream()); }
	public Iterator<Game> iterator() { return list.iterator(); }
	
	public Settings getSettings() { return settings; }
	public AssetManager getAssetManager() { return info().getAssetManager(); }
	
	public boolean doesSupportAttribute(Attribute attribute) { return Arrays.stream(attributes).anyMatch( a -> a == attribute); }
	public final Attribute[] getSupportedAttributes() { return attributes; }
	
	public Scanner getScanner() { return scanner; }
			
	public boolean canBeLoaded()
	{
	  return Files.exists(datPath());
	}
	
	public final void load()
	{ 
	  if (!loaded)
	  {
	    DatLoader.Data data = info.getLoader().load(this);
	    
	    list = data.games;
	    clones = data.clones;
	    info.computeStats(this);
	    
	    loaded = true;
	  }
	}
	
	@Override
  public String toString()
	{
		return platform.name+" ("+info.getName()+")";
	}
	
	public String ident()
	{
		return info.getFormat().getIdent()+"-"+platform.tag+"-"+info.getProvider().getTag()+info.getProvider().builtSuffix();
	}
	
	public Path datPath()
	{
		return Paths.get("dat/"+ident()+"."+info.getFormat().getExtension());
	}
	
	public Path getAttachmentPath()
	{
	  return settings.romsPath.resolve(Paths.get("attachments"));
	}
	
	public Searcher<Game> getSearcher()
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
  	  
      Gson gson = Json.prebuild().registerTypeAdapter(GameList.class, new GameListAdapter(list)).create();
      
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
  	    
  	    Gson gson = Json.prebuild().registerTypeAdapter(GameList.class, new GameListAdapter(list)).create();
  	    
  	    
  	    try (BufferedReader rdr = Files.newBufferedReader(statusPath))
  	    {
  	      gson.fromJson(rdr, GameList.class);
  	      refreshStatus();
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

  public boolean hasFeature(Feature feature)
  {
    if (feature == Feature.SINGLE_ROM_PER_GAME)
      return !hasMultipleRomsPerGame();
    else
      return false;
  }
  
  public List<Game> filter(String query)
  { 
    return stream()
      .filter(searcher.search(query))
      .collect(Collectors.toList());
  }
  
  public Game find(String search) 
  { 
    Optional<Game> rom = list.stream().filter(getSearcher().search(search)).findFirst();
    if (!rom.isPresent()) throw new RuntimeException("GameSet::find failed to find any rom");
    return rom.orElse(null);
  }
}
