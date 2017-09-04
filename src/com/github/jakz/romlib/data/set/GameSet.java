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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetCache;
import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.pixbits.lib.io.digest.HashCache;
import com.pixbits.lib.log.Log;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.romset.GameSetFeatures;
import jack.rm.json.Json;
import jack.rm.json.GameListAdapter;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public class GameSet implements Iterable<Game>, GameMap
{
  public static GameSet current = null;
	
  private boolean loaded;

  private final Platform platform;
  private final List<DataSupplier> loaders;
  private final GameSetInfo info;

	private GameList list;
	private CloneSet clones;
	
	private Settings settings;
	
	private GameSetFeatures helper;
	
	private final AssetCache assetCache;
	private final Attribute[] attributes;

	public GameSet(Platform platform, Provider provider, DataSupplier loader, DatFormat format, Attribute[] attributes, AssetManager assetManager, Feature... features)
	{
		Objects.requireNonNull(platform);
	  this.info = new GameSetInfo(provider, format, assetManager);
	  this.loaders = Collections.singletonList(loader);
		this.list = null;
	  this.clones = null;
	  this.platform = platform;
		this.attributes = attributes;
		this.loaded = false;
		this.assetCache = new AssetCache();
		this.helper = new GameSetFeatures(this, features);
	}
	
	public GameSet(Platform platform, Provider provider, DataSupplier loader)
  {
	  Objects.requireNonNull(platform);
	  this.info = new GameSetInfo(provider);
	  this.loaders = Collections.singletonList(loader);
	  this.list = null;
	  this.clones = null;
	  this.platform = platform;
	  this.attributes = new Attribute[0];
	  this.loaded = false;
	  this.assetCache = new AssetCache();
	  this.helper = new GameSetFeatures(this);
  }
	
	public GameSet(Platform platform, Provider provider, GameList list, CloneSet clones)
	{
	  Objects.requireNonNull(platform);
	  this.info = new GameSetInfo(provider);
	  this.loaders = null;
	  this.list = list;
	  setClones(clones, true);
	  this.platform = platform;
	  this.attributes = new Attribute[0];
	  this.loaded = true;
	  this.assetCache = new AssetCache();
	  this.helper = new GameSetFeatures(this);
	}

	public void setClones(CloneSet clones) { setClones(clones, true); }
	public void setClones(CloneSet clones, boolean assignOrphanedGames)
	{ 
    this.clones = clones;
    
    if (clones != null)
    {
      if (assignOrphanedGames)
        assignOrphanedGamesToClones();
      
      for (GameClone clone : clones)
        for (Game game : clone)
          game.setClone(clone);
    }
	}
	
	private void assignOrphanedGamesToClones()
	{
	  if (this.clones != null)
	  {
	    List<GameClone> orphanClones = stream()
	      .filter(game -> clones.get(game) == null)
	      .map(game -> new GameClone(game))
	      .collect(Collectors.toList());
	    
	    List<GameClone> clones = this.clones.stream().collect(Collectors.toList());
	    clones.addAll(orphanClones);
	    
	    this.clones = new CloneSet(clones.toArray(new GameClone[clones.size()]));
	  }
	}
	
	public Platform platform() { return platform; }
	public GameSetFeatures helper() { return helper; }
	public CloneSet clones() { return clones; }
	public GameSetInfo info() { return info; }
	public GameSetStatus status() { return list.status(); }
	public HashCache<Rom> hashCache() { return list.cache(); }
	public RomSize.Set sizeSet() { return list.sizeSet(); }
	public boolean hasMultipleRomsPerGame() { return list.hasMultipleRomsPerGame(); }
	
	public void checkNames() { list.checkNames(); }
	public void resetStatus() { list.resetStatus(); }
	public void refreshStatus() { list.refreshStatus(); }
	
	public Game getAny() { return get(0); }
	public Game get(int index) { return list.get(index); }
	@Override public Game get(String title) { return list.get(title); }
	public int gameCount() { return list.gameCount(); }
	public Stream<Game> stream() { return games(); }
	public Stream<Game> games() { return list.stream(); }
	public Stream<Game> orphanGames() { return games().filter(g -> !g.hasClone()); }
	public Stream<Rom> romStream() { return list.stream().flatMap(g -> g.stream()); }
	public Iterator<Game> iterator() { return list.iterator(); }
	
	public Stream<Rom> foundRoms()
  { 
    return list.stream()
      .map(game -> game.stream()
          .filter(r -> r.handle() != null))
      .reduce((s1,s2) -> Stream.concat(s1, s2))
      .get(); 
  }
	
	public Settings getSettings() { return settings; }
	public AssetManager getAssetManager() { return info().getAssetManager(); }
	
	public boolean doesSupportAttribute(Attribute attribute) { return Arrays.stream(attributes).anyMatch( a -> a == attribute); }
	public final Attribute[] getSupportedAttributes() { return attributes; }
	
	public AssetCache assetCache() { return assetCache; }
  	
	public boolean canBeLoaded()
	{
	  return Files.exists(datPath());
	}
	
	public final void load()
	{ 
	  if (!loaded)
	  {
	    loaders.stream().forEach(l -> {
	      DataSupplier.Data data = l.load(this);
	      
	      list = data.games.orElse(null);
	      data.clones.ifPresent(this::setClones);
	      info.computeStats(this);
	      
	      loaded = true;
	    });
	  }
	}
	
	@Override
  public String toString()
	{
		return platform.name+" ("+info.getCaption()+")";
	}
	
	public String ident()
	{
		return info.getFormat().getIdent()+"-"+platform.tag+"-"+info.getProvider().getIdentifier();
	}
	
	public Path datPath()
	{
		return Paths.get("dat/"+ident()+"."+info.getFormat().getExtension());
	}
	
	public Path getAttachmentPath()
	{
	  return settings.romsPath.resolve(Paths.get("attachments"));
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
    else if (feature == Feature.CLONES)
      return clones != null && clones.size() > 0;
    else
      return helper.hasFeature(feature);
  }
  
  public List<Game> filter(String query)
  { 
    return stream()
      .filter(helper.searcher().search(query))
      .collect(Collectors.toList());
  }
  
  public Game find(String search) 
  { 
    Optional<Game> rom = list.stream().filter(helper.searcher().search(search)).findFirst();
    if (!rom.isPresent()) throw new RuntimeException("GameSet::find failed to find any rom");
    return rom.orElse(null);
  }
}
