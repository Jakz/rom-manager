package jack.rm.data.romset;

import java.util.*;
import java.util.stream.Collectors;

import com.pixbits.plugin.PluginManager;

import jack.rm.Main;
import jack.rm.data.console.System;
import jack.rm.data.rom.RomSize;
import jack.rm.log.*;
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
	    
	    for (System system : System.values())
	    {
	      if (plugin.isSystemSupported(system))
	        sets.put(system, plugin.buildRomSet(datParsers, system));
	    }
	  }
	}
  
  private static Map<System, RomSet> sets = new HashMap<>();

	public static RomSet bySystem(System system)
	{
	   return sets.values().stream().filter( rs -> rs.system == system).findFirst().orElse(null);
	}
	
	public static RomSet byIdent(String ident)
	{
		return sets.values().stream().filter( rs -> rs.ident().equals(ident)).findFirst().orElse(null);
	}
	
	public static Collection<RomSet> sets()
	{
		return sets.values();
	}
	
	public static RomSet loadSet(System console)
	{
		return loadSet(sets.get(console));
	}
	
	public static RomSet loadSet(RomSet set)
	{
		Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(set), "Loading romset");
	  			
		RomSize.mapping.clear();
		set.load();
						
		return set;
	}
}
