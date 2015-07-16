package jack.rm;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.google.gson.reflect.TypeToken;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.json.Json;
import jack.rm.log.*;

public class RomJsonState
{
	int number;
	RomFileEntry file;
	RomStatus status;
	
	public RomJsonState() { }
	
	public RomJsonState(int number, RomStatus status, RomFileEntry file)
	{
	  this.number = number;
	  this.status = status;
	  this.file = file;
	}
	
	public static void consolidate(RomList list)
	{
		RomJsonState[] roms = list.stream()
		.filter( r -> r.status != RomStatus.NOT_FOUND)
		.map( r -> new RomJsonState(((NumberedRom)r).number, r.status, r.entry) )
		.toArray( size -> new RomJsonState[size]);

    File folder = new File("data/"+RomSet.current.ident()+"/");
    folder.mkdirs();
		
		try (FileWriter writer = new FileWriter(folder + "/status.json"))
		{
			writer.write(Json.build().toJson(roms));
		  Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(RomSet.current), "Romset status saved on json");

		}
		catch (IOException e )
		{
			e.printStackTrace();
		}
		
	}
	
	public static boolean load(RomList list)
	{
		try
		{
			String fileName = "data/"+list.set.ident()+"/"+"status.json";
			
			if (new File(fileName).exists())
			{
				List<RomJsonState> jroms = Json.build().fromJson(new FileReader(fileName), new TypeToken<List<RomJsonState>>(){}.getType());

				for (RomJsonState prom : jroms)
				{
					Rom rom = list.getByNumber(prom.number);
					
					if (rom != null)
					{
					  rom.entry = prom.file;
	          rom.status = prom.status;
					}
					
				}
				
				return true;
			}
			else
				return false;
		}
		catch (IOException e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
}
