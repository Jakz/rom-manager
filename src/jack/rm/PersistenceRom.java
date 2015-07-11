package jack.rm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.log.*;

public class PersistenceRom
{
	int number;
	RomFileEntry file;
	RomStatus status;
	
	public PersistenceRom() { }
	
	public PersistenceRom(int number, RomStatus status, RomFileEntry file)
	{
	  this.number = number;
	  this.status = status;
	  this.file = file;
	}
	
	public static void consolidate(RomList list)
	{
		List<PersistenceRom> roms = new ArrayList<PersistenceRom>();
		
		int s = list.count();
		
		for (int i = 0; i < s; ++i)
		{
			Rom r = list.get(i);
			
			if (r.status != RomStatus.NOT_FOUND)
			{
				PersistenceRom pr = new PersistenceRom(r.number, r.status, r.entry);	
				roms.add(pr);
			}
		}
		
		PersistenceRom[] romsa = roms.toArray(new PersistenceRom[roms.size()]);
		
		try
		{
			File folder = new File("data/"+RomSet.current.ident()+"/");
			folder.mkdirs();
			
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(folder+"/status.json"));

			dos.writeBytes(Settings.loader.toJson(romsa, PersistenceRom[].class));
			
			dos.close();
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
		
		Log.log(LogType.MESSAGE, LogSource.STATUS, LogTarget.romset(RomSet.current), "Romset status saved on json");
	}
	
	public static boolean load(RomSet set)
	{
		try
		{
			String fileName = "data/"+set.ident()+"/"+"status.json";
			
			if (new File(fileName).exists())
			{
				PersistenceRom[] proms = Settings.loader.fromJson(new FileReader(fileName), PersistenceRom[].class);
				
				for (PersistenceRom prom : proms)
				{
					Rom rom = Main.romList.getByNumber(prom.number);
					
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
				
			/*File file = new File("data/settings.json");
			
			if (file.exists())
			{
				
				Settings[] sts = loader.fromJson(new FileReader(file), Settings[].class);
				
				for (Settings s : sts)
				{
					settings.put(s.set, s);
				}
			}*/
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
}
