package jack.rm;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import jack.rm.data.Asset;
import jack.rm.data.Rom;
import jack.rm.data.set.*;
import jack.rm.files.OrganizerDetails;
import jack.rm.json.Json;
import jack.rm.plugin.folder.FolderPlugin;
import jack.rm.plugin.PluginRealType;
import jack.rm.plugin.PluginSet;

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
	public Path unknownPath;
	
	public boolean checkImageCRC;
	
	public PluginSet plugins;
		
	public OrganizerDetails organizer;
	
	public Settings()
	{
	  plugins = new PluginSet();
	}
	
	public FolderPlugin getFolderOrganizer() { return plugins.getPlugin(PluginRealType.FOLDER_ORGANIZER); }
	
	public Set<Path> getIgnoredPaths()
	{
	  Set<Path> paths = new HashSet<>();
	  
	  if (organizer.shouldMoveUnknownFiled() && unknownPath != null)
	    paths.add(unknownPath);
	  
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
		unknownPath = null;
	}
	
  public static Path getAssetPath(Asset asset)
  {
  	Path path = Paths.get("screens/").resolve(RomSet.current.ident());	
  	return path.resolve(asset == Asset.SCREEN_GAMEPLAY ? "game/" : "title/");
  }
}
