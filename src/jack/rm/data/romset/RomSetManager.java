package jack.rm.data.romset;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.platforms.Platform;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.data.rom.RomSize;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.datparsers.DatParserPlugin;
import jack.rm.plugins.providers.ProviderPlugin;


public class RomSetManager
{
	@SuppressWarnings("unchecked")
  public static void buildRomsetList()
	{
	  PluginManager<ActualPlugin, ActualPluginBuilder> manager = Main.manager; 
	  
	  Set<ActualPluginBuilder> parsers = manager.getBuildersByType(PluginRealType.DAT_PARSER);
	  List<DatParserPlugin> datParsers = parsers.stream()
	                                            .map(b -> (DatParserPlugin)manager.build((Class<DatParserPlugin>)b.getID().getType())).collect(Collectors.toList());
	  
	  Set<ActualPluginBuilder> builders = manager.getBuildersByType(PluginRealType.PROVIDER);

	  for (ActualPluginBuilder builder : builders)
	  {
	    ProviderPlugin plugin = (ProviderPlugin)manager.build((Class<ProviderPlugin>)builder.getID().getType());
	    
	    RomSet[] rsets = plugin.buildRomSets(datParsers);
	    
	    for (RomSet set : rsets)
	    {
	      List<RomSet> setsForSystem = sets.computeIfAbsent(set.platform, s -> new ArrayList<>());
	      setsForSystem.add(set);
	    }
	  }
	  
	  for (Platform platform : Platform.values())
	    sets.computeIfAbsent(platform, s -> new ArrayList<>());
	}
  
  private static Map<Platform, List<RomSet>> sets = new HashMap<>();

	public static List<RomSet> bySystem(Platform platform)
	{
	  return sets.get(platform); 
	}
	
	public static RomSet byIdent(String ident)
	{
		for (List<RomSet> sets : RomSetManager.sets.values())
		{
		  Optional<RomSet> rs = sets.stream().filter(set -> set.ident().equals(ident)).findFirst();
		  
		  if (rs.isPresent())
		    return rs.get();
		}
		
		return null;
	}
	
	public static Collection<RomSet> allSets()
	{
		List<RomSet> allSets = new ArrayList<>();
		
		sets.values().forEach(allSets::addAll);
	  
	  return allSets;
	}
	
	public static RomSet loadSet(String ident) throws FileNotFoundException
	{
		return loadSet(byIdent(ident));
	}
	
	public static RomSet loadSet(RomSet set) throws FileNotFoundException
	{
		if (!Files.exists(set.datPath()))
		  throw new FileNotFoundException("missing DAT files");
	  
		Log.getLogger(LogSource.STATUS).i(LogTarget.romset(set), "Loading romset");
	  			
		RomSize.mapping.clear();
		set.load();
						
		return set;
	}
}
