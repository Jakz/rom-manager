package jack.rm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;

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
		List<RomJsonState> roms = new ArrayList<RomJsonState>();
		
		int s = list.count();
		
		for (int i = 0; i < s; ++i)
		{
			Rom r = list.get(i);
			
			if (r.status != RomStatus.NOT_FOUND)
			{
				RomJsonState pr = new RomJsonState(((NumberedRom)r).number, r.status, r.entry);	
				roms.add(pr);
			}
		}
		
		RomJsonState[] romsa = roms.toArray(new RomJsonState[roms.size()]);
		
		try
		{
			File folder = new File("data/"+RomSet.current.ident()+"/");
			folder.mkdirs();
			
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(folder+"/status.json"));

			dos.writeBytes(Json.build().toJson(romsa, RomJsonState[].class));
			
			dos.close();
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
		
		Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(RomSet.current), "Romset status saved on json");
	}
	
	public static boolean load(RomList list)
	{
		try
		{
			String fileName = "data/"+list.set.ident()+"/"+"status.json";
			
			if (new File(fileName).exists())
			{
				RomJsonState[] proms = Json.build().fromJson(new FileReader(fileName), RomJsonState[].class);
				
				for (RomJsonState prom : proms)
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
		catch (Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
}
