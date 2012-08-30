package jack.rm;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.*;

import jack.rm.data.set.*;

public class Settings
{
	private static Map<RomSet, Settings> settings = new HashMap<RomSet, Settings>(); 
	public final static Gson loader;
	
	static
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(RomSet.class, new RomSetSerializer());
		loader = builder.setPrettyPrinting().create();
	}
	
	private static class RomSetSerializer implements JsonSerializer<RomSet>, JsonDeserializer<RomSet> {
		  public JsonElement serialize(RomSet src, Type typeOfSrc, JsonSerializationContext context) {
		    return new JsonPrimitive(src.ident());
		  }
		  
		  public RomSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			  	return RomSetManager.byIdent(json.getAsJsonPrimitive().getAsString());
			  }
		}
	
	public static Settings get(RomSet set)
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
	
	public RomSet set;
	public String renamingPattern;
	public String romsPath;
	public String unknownPath;
	
	public boolean checkImageCRC;
	public boolean checkInsideArchives;
	public boolean moveUnknownFiles;
	
	public boolean useRenamer;
	public boolean renameInsideZips;
	
	Settings()
	{
		
	}
	
	Settings(RomSet set)
	{
		this.set = set;
		
		checkImageCRC = true;
		checkInsideArchives = true;
		moveUnknownFiles = false;
		renameInsideZips = false;
		useRenamer = false;
		
		renamingPattern = "%n - %t [%S]";
		romsPath = null;
		unknownPath = null;
	}

}
