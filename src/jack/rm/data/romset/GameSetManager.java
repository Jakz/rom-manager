package jack.rm.data.romset;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.datparsers.DatParserPlugin;
import jack.rm.plugins.providers.ProviderPlugin;


public class GameSetManager
{
  private static final Logger logger = Log.getLogger(GameSetManager.class);
  
  private final Map<Platform, List<GameSet>> sets = new HashMap<>();
  private final Map<GameSet, GameSetFeatures> helpers = new HashMap<>();
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
	      helpers.put(set, new GameSetFeatures(set));
	      
	      List<GameSet> setsForPlatform = sets.computeIfAbsent(set.platform(), s -> new ArrayList<>());
	      setsForPlatform.add(set);
	    }
	  }
	  
	  for (Platform platform : Platform.values())
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
}
