package jack.rm.data.romset;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameList;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.GameSetFeatures;
import com.github.jakz.romlib.json.GameListAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.plugin.PluginManager;

import jack.rm.GlobalSettings;
import jack.rm.json.Json;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.types.DatParserPlugin;
import jack.rm.plugins.types.ProviderPlugin;


public class GameSetManager
{
  private static final Logger logger = Log.getLogger(GameSetManager.class);
  
  private final Map<Platform, List<GameSet>> sets = new HashMap<>();
  
  private final Map<GameSet, MyGameSetFeatures> helpers = new HashMap<>();
  private final Map<GameSet, Settings> settings = new HashMap<>();
  
  private final PluginManager<ActualPlugin, ActualPluginBuilder> manager;
  
  public GameSetManager(PluginManager<ActualPlugin, ActualPluginBuilder> manager)
  {
    this.manager = manager;
  }

  @SuppressWarnings("unchecked")
  public void buildRomsetList()
	{	  
	  Set<ActualPluginBuilder> parsers = manager.getBuildersByType(PluginRealType.DAT_PARSER);
	  List<DatParserPlugin> datParsers = parsers.stream()
	                                            .map(b -> (DatParserPlugin)manager.build((Class<DatParserPlugin>)b.getID().getType())).collect(Collectors.toList());
	  
	  Set<ActualPluginBuilder> builders = manager.getBuildersByType(PluginRealType.PROVIDER);

	  logger.d("Building available rom sets");
	  logger.ld("Found %d dat parsers: %s", () -> datParsers.size(), () -> datParsers.stream().map(p -> Arrays.toString(p.getSupportedFormats())).collect(Collectors.joining(", ")));
	  
	  for (ActualPluginBuilder builder : builders)
	  {
	    ProviderPlugin plugin = (ProviderPlugin)manager.build((Class<ProviderPlugin>)builder.getID().getType());
	    
	    logger.d("Found ProviderPlugin: %s", plugin.getClass().getName());

	    
	    GameSet[] rsets = plugin.buildRomSets(datParsers);
	    
	    for (GameSet set : rsets)
	    {
	      helpers.put(set, new MyGameSetFeatures(set));
	      
	      List<GameSet> setsForPlatform = sets.computeIfAbsent(set.platform(), s -> new ArrayList<>());
	      setsForPlatform.add(set);
	    }
	  }
	  
	  for (Platform platform : Platforms.values())
	    sets.computeIfAbsent(platform, s -> new ArrayList<>());
	}
  

	public List<GameSet> bySystem(Platform platform)
	{
	  return sets.get(platform); 
	}
	
	public GameSet byIdent(String ident)
	{
		for (List<GameSet> sets : sets.values())
		{
		  Optional<GameSet> rs = sets.stream().filter(set -> set.ident().equals(ident)).findFirst();
		  
		  if (rs.isPresent())
		    return rs.get();
		}
		
		return null;
	}
	
	public Settings settings(GameSet set) { return settings.get(set); }
	public GameSetFeatures helpers(GameSet set) { return helpers.get(set); }
	
	public Collection<GameSet> allSets()
	{
		List<GameSet> allSets = new ArrayList<>();
		
		sets.values().forEach(allSets::addAll);
	  
	  return allSets;
	}
	
	public GameSet loadSet(String ident) throws FileNotFoundException
	{
		return loadSet(byIdent(ident));
	}
	
	public GameSet loadSet(GameSet set) throws FileNotFoundException
	{
		if (!Files.exists(set.datPath()))
		  throw new FileNotFoundException("missing DAT files");
	  
		Log.getLogger(LogSource.STATUS).i(LogTarget.romset(set), "Loading romset");

		set.load();
						
		return set;
	}
	
	public boolean loadSetStatus(GameSet set)
	{
	  try
    {
	    Path basePath = Paths.get("data/", set.ident());     
      Path settingsPath = basePath.resolve("settings.json");
      
      try
      {
        AssetManager assetManager = set.getAssetManager();
        for (Asset asset : assetManager.getSupportedAssets())
          Files.createDirectories(set.getAssetPath(asset,false));
      }
      catch (IOException e)
      {
        e.printStackTrace();
        // TODO: log
      }
      
      if (!Files.exists(settingsPath))
      {
        logger.d("Unable to load game status for %s: no saved status found.", set.toString());
        settings.put(set, new Settings(manager, Arrays.asList(set.getSupportedAttributes())));
        return false;
      }
      else
      {
        try (BufferedReader rdr = Files.newBufferedReader(settingsPath))
        {
          Settings settings = Json.build().fromJson(rdr, Settings.class);
          
          if (settings == null)
            throw new JsonParseException("Unable to load settings for gameset "+set);
          
          this.settings.put(set, settings);
          logger.d("Loaded gameset status for %s", set.toString());
        }
        catch (JsonParseException e)
        {
          if (e.getCause() instanceof ClassNotFoundException)
            Log.getLogger(LogSource.STATUS).e("Error while loading plugin state: %s", e.getCause().toString());
          
          e.printStackTrace();
        }
        
        Path statusPath = basePath.resolve("status.json");
        
        Gson gson = Json.prebuild().registerTypeAdapter(GameList.class, new GameListAdapter(set.list())).create();

        try (BufferedReader rdr = Files.newBufferedReader(statusPath))
        {
          gson.fromJson(rdr, GameList.class);
          set.refreshStatus();
          
          logger.d("Current status: %d/%d roms in %d/%d/%d games", set.status().getFoundRomsCount(), set.info().romCount(), set.status().getCorrectCount(), set.status().getIncompleteCount(), set.info().gameCount());
          
          if (set.hasFeature(Feature.CLONES))
            set.clones().updateStatus();
          
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
	
	 public void saveSetStatus(GameSet set)
	  {
	    try
	    {
	      Path basePath = GlobalSettings.DATA_PATH.resolve(set.ident());
	      
	      Files.createDirectories(basePath);
	      
	      Path settingsPath = basePath.resolve("settings.json");
	      
	      try (BufferedWriter wrt = Files.newBufferedWriter(settingsPath))
	      {
	        MyGameSetFeatures helper = set.helper();
	        wrt.write(Json.build().toJson(helper.settings(), Settings.class));
	      }
	      
	      Path statusPath = basePath.resolve("status.json");
	      
	      Gson gson = Json.prebuild().registerTypeAdapter(GameList.class, new GameListAdapter(set.list())).create();
	      
	      try (BufferedWriter wrt = Files.newBufferedWriter(statusPath))
	      {
	        wrt.write(gson.toJson(set.list()));
	        Log.getLogger(LogSource.STATUS).i(LogTarget.romset(set), "Romset status saved on json");
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }
}
