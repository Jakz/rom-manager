package jack.rm;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.*;

import jack.rm.data.Asset;
import jack.rm.data.Rom;
import jack.rm.data.RomFileEntry;
import jack.rm.data.set.*;
import jack.rm.files.FolderPolicy;
import jack.rm.files.OrganizerDetails;
import jack.rm.json.JsonPluginAdapter;
import jack.rm.plugin.folder.FolderPlugin;

public class Settings
{
	private static Map<RomSet<? extends Rom>, Settings> settings = new HashMap<>(); 
	public final static Gson loader;
	
	static
	{
		GsonBuilder builder = new GsonBuilder()
		.registerTypeAdapter(RomSet.class, new RomSetSerializer())
		.registerTypeAdapter(RomFileEntry.class, new RomFileEntry.Adapter())
		.registerTypeAdapter(Path.class, new PathSerializer())
		.registerTypeAdapter(FolderPlugin.class, new JsonPluginAdapter<FolderPlugin>());
		
		loader = builder.setPrettyPrinting().create();
	}
	
	private static class RomSetSerializer implements JsonSerializer<RomSet<? extends Rom>>, JsonDeserializer<RomSet<? extends Rom>> {
		  @Override
      public JsonElement serialize(RomSet<? extends Rom> src, Type typeOfSrc, JsonSerializationContext context) {
		    return new JsonPrimitive(src.ident());
		  }
		  
		  @Override
      public RomSet<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			  	return RomSetManager.byIdent(json.getAsJsonPrimitive().getAsString());
			  }
		}
	
	private static class PathSerializer implements JsonSerializer<Path>, JsonDeserializer<Path> {
	  @Override
    public JsonElement serialize(Path src, Type type, JsonSerializationContext context)
	  {
	    return new JsonPrimitive(src.toString());
	  }
	  
	  @Override
    public Path deserialize(JsonElement json, Type type, JsonDeserializationContext context)
	  {
	    return java.nio.file.Paths.get(json.getAsString());
	  }
	}
	
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
				
				Settings[] sts = loader.fromJson(new FileReader(file), Settings[].class);
				
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

			dos.writeBytes(loader.toJson(sts, Settings[].class));
			
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
		
	public OrganizerDetails organizer;
	public int folderSize;
	
	Settings()
	{

	}
	
	public Set<Path> getIgnoredPaths()
	{
	  Set<Path> paths = new HashSet<>();
	  
	  if (organizer.shouldMoveUnknownFiled() && unknownPath != null)
	    paths.add(unknownPath);
	  
	  return paths;
	}
	
	Settings(RomSet<?> set)
	{
		this.set = set;
		
		checkImageCRC = true;
		folderSize = 100;
		
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
