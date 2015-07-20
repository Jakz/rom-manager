package jack.rm;

import java.nio.file.Path;
import java.util.*;

import com.pixbits.plugin.PluginManager;
import com.pixbits.plugin.PluginSet;

import jack.rm.plugins.*;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

public class Settings
{	
	public String renamingPattern;
	public Path romsPath;	
	public boolean checkImageCRC;
	
	public PluginSet<ActualPlugin> plugins;
			
	
	public Settings(PluginManager<ActualPlugin, ActualPluginBuilder> manager)
	{
	  plugins = new PluginSet<ActualPlugin>();
	  manager.setup(plugins);
	  checkImageCRC = true;   
	  renamingPattern = "%n - %t [%S]";
	  romsPath = null;
	}
	
	public RenamerPlugin getRenamer()
	{
	  RenamerPlugin plugin = plugins.getEnabledPlugin(PluginRealType.RENAMER);
	  return plugin;
	}
	
	public FolderPlugin getFolderOrganizer()
	{ 
	  FolderPlugin plugin = plugins.getEnabledPlugin(PluginRealType.FOLDER_ORGANIZER);
	  return plugin != null ? plugin : null;
	}
	
	public boolean hasCleanupPlugins()
	{
	  return !plugins.getEnabledPlugins(PluginRealType.ROMSET_CLEANUP).isEmpty();
	}
	
	public Set<Path> getIgnoredPaths()
	{
	  Set<Path> paths = new HashSet<>();
	  
	  plugins.stream().filter( p -> p instanceof PluginWithIgnorePaths ).forEach( p -> {
	    Set<Path> ipaths = ((PluginWithIgnorePaths)p).getIgnoredPaths();
	    ipaths.stream().filter(Objects::nonNull).forEach(paths::add);
	  });

	  return paths;
	}
	
	Settings()
	{
    plugins = new PluginSet<ActualPlugin>();
	}
}
