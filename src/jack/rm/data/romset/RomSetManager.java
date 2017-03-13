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

import com.pixbits.lib.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.data.console.System;
import jack.rm.data.rom.RomSize;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;
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
	      List<RomSet> setsForSystem = sets.computeIfAbsent(set.system, s -> new ArrayList<>());
	      setsForSystem.add(set);
	    }
	  }
	  
	  for (System system : System.values())
	    sets.computeIfAbsent(system, s -> new ArrayList<>());
	}
  
  private static Map<System, List<RomSet>> sets = new HashMap<>();

	public static List<RomSet> bySystem(System system)
	{
	  return sets.get(system); 
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
	  
	  Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(set), "Loading romset");
	  			
		RomSize.mapping.clear();
		set.load();
						
		return set;
	}
}
