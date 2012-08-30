package jack.rm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;

public class PersistenceRom
{
	int number;
	String path;
	RomStatus status;
	
	public static void consolidate(RomList list)
	{
		Main.logln("Saving romset status on json..");
		
		List<PersistenceRom> roms = new ArrayList<PersistenceRom>();
		
		int s = list.count();
		
		for (int i = 0; i < s; ++i)
		{
			Rom r = list.get(i);
			
			if (r.status != RomStatus.NOT_FOUND)
			{
				PersistenceRom pr = new PersistenceRom();
				pr.status = r.status;
				pr.number = r.number;
				pr.path = r.path.toString();
				
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
	}
}
