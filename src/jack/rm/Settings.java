package jack.rm;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.JsonParseException;
import com.pixbits.plugin.PluginManager;
import com.pixbits.plugin.PluginSet;

import jack.rm.data.Asset;
import jack.rm.data.Rom;
import jack.rm.data.set.*;
import jack.rm.files.OrganizerDetails;
import jack.rm.json.Json;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogType;
import jack.rm.plugins.*;
import jack.rm.plugins.folder.FolderPlugin;

public class Settings
{	
	public String renamingPattern;
	public Path romsPath;	
	public boolean checkImageCRC;
	
	public PluginSet<ActualPlugin> plugins;
		
	public OrganizerDetails organizer;
	
	
	public Settings(PluginManager<ActualPlugin, ActualPluginBuilder> manager)
	{
	  plugins = new PluginSet<ActualPlugin>();
	  manager.setup(plugins);
	  checkImageCRC = true;   
	  organizer = new OrganizerDetails();
	  renamingPattern = "%n - %t [%S]";
	  romsPath = null;
	}
	
	public FolderPlugin getFolderOrganizer()
	{ 
	  FolderPlugin plugin = plugins.getPlugin(PluginRealType.FOLDER_ORGANIZER);
	  return plugin != null && plugin.isEnabled() ? plugin : null;
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
	
  public static Path getAssetPath(Asset asset)
  {
  	Path path = Paths.get("screens/").resolve(RomSet.current.ident());	
  	return path.resolve(asset == Asset.SCREEN_GAMEPLAY ? "game/" : "title/");
  }
}
