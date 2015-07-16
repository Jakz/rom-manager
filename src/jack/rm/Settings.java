package jack.rm;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.JsonParseException;
import com.pixbits.plugin.PluginSet;

import jack.rm.data.Asset;
import jack.rm.data.Rom;
import jack.rm.data.set.*;
import jack.rm.files.OrganizerDetails;
import jack.rm.json.Json;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogType;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.PluginWithIgnorePaths;
import jack.rm.plugins.folder.FolderPlugin;

public class Settings
{
	private static Map<RomSet<? extends Rom>, Settings> settings = new HashMap<>(); 

	public static Settings get(RomSet<?> set)
	{
		Settings s = settings.get(set);
		
		if (s == null)
		{
			s = new Settings(set);
			settings.put(set, s);
		}
		
		return s;
	}
	
	public static Settings current()
	{
		return settings.get(RomSet.current);
	}
	
	public static void load()
	{
		try
		{
			File file = new File("data/settings.json");
			
			if (file.exists())
			{
				
				Settings[] sts = Json.build().fromJson(new FileReader(file), Settings[].class);
				
				for (Settings s : sts)
				{
					settings.put(s.set, s);
				}
			}
		}
		catch (JsonParseException e)
		{
		  if (e.getCause() instanceof ClassNotFoundException)
		    Log.log(LogType.ERROR, LogSource.PLUGINS, null, "Error while loading plugin state: "+e.getCause().toString());
		  
		  e.printStackTrace();
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public static void consolidate()
	{
		try
		{			
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/settings.json"));
			
			Settings[] sts = settings.values().toArray(new Settings[settings.values().size()]);

			dos.writeBytes(Json.build().toJson(sts, Settings[].class));
			
			dos.close();
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public RomSet<?> set;
	public String renamingPattern;
	public Path romsPath;	
	public boolean checkImageCRC;
	
	public PluginSet<ActualPlugin> plugins;
		
	public OrganizerDetails organizer;
	
	public Settings()
	{
	  plugins = new PluginSet<ActualPlugin>();
	  /*plugins.add(ActualPlugin.manager.build(jack.rm.plugins.folder.NumericalOrganizer.class));
	  plugins.add(ActualPlugin.manager.build(jack.rm.plugins.folder.AlphabeticalOrganizer.class));
	  plugins.add(ActualPlugin.manager.build(jack.rm.plugins.cleanup.DeleteEmptyFoldersPlugin.class));
	  plugins.add(ActualPlugin.manager.build(jack.rm.plugins.cleanup.MoveUnknownFilesPlugin.class));  
	  plugins.add(ActualPlugin.manager.build(jack.rm.plugins.renamer.BasicPatternSet.class));  
    pugins.add(ActualPlugin.manager.build(jack.rm.plugins.renamer.NumberedRomPattern.class));*/  
    //plugins.add(ActualPlugin.manager.build(jack.rm.plugins.renamer.RenamerPlugin.class));  
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
	
	Settings(RomSet<?> set)
	{
		this();
		
	  this.set = set;
		
		checkImageCRC = true;
		
		organizer = new OrganizerDetails();
		renamingPattern = "%n - %t [%S]";
		romsPath = null;
	}
	
  public static Path getAssetPath(Asset asset)
  {
  	Path path = Paths.get("screens/").resolve(RomSet.current.ident());	
  	return path.resolve(asset == Asset.SCREEN_GAMEPLAY ? "game/" : "title/");
  }
}
