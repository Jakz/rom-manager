package jack.rm;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.pixbits.plugin.PluginManager;
import com.pixbits.plugin.PluginSet;

import jack.rm.data.console.System;
import jack.rm.data.rom.Attribute;
import jack.rm.plugins.*;
import jack.rm.plugins.downloader.RomDownloaderPlugin;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;
import jack.rm.plugins.searcher.SearchPlugin;

public class Settings
{	
  public String renamingPattern;
  public String internalRenamingPattern;
  public boolean shouldRenameInternalName;
	public Path romsPath;	
	
	public PluginSet<ActualPlugin> plugins;
	
	public List<Attribute> attributes;

  Settings()
  {
    plugins = new PluginSet<ActualPlugin>();
    attributes = new ArrayList<>();
  }
  
	public Settings(PluginManager<ActualPlugin, ActualPluginBuilder> manager, List<Attribute> attributes)
	{
	  this();
	  this.attributes = attributes;
	  manager.setup(plugins); 
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
	
	public SearchPlugin getSearchPlugin()
	{
	  SearchPlugin plugin = plugins.getEnabledPlugin(PluginRealType.SEARCH);
	  return plugin != null ? plugin : null;
	}
	
	public boolean hasCleanupPlugins()
	{
	  return !plugins.getEnabledPlugins(PluginRealType.ROMSET_CLEANUP).isEmpty();
	}
	
	public boolean hasDownloader(System system)
	{
	  Set<RomDownloaderPlugin> downloaders = plugins.getEnabledPlugins(PluginRealType.ROM_DOWNLOADER);
	  
	  return downloaders.stream().filter( p -> p.isSystemSupported(system)).findFirst().isPresent();
	}
		
	public Set<Path> getIgnoredPaths()
	{
	  Set<Path> paths = new HashSet<>();
	  
	  plugins.stream().filter( p -> p instanceof PluginWithIgnorePaths ).forEach( p -> {
	    Set<Path> ipaths = ((PluginWithIgnorePaths)p).getIgnoredPaths();
	    ipaths.stream().filter(Objects::nonNull).forEach(paths::add);
	  });
	  
	  paths.add(Paths.get("attachments"));

	  return paths;
	}
	
	public List<Attribute> getRomAttributes() { return attributes; }
}
