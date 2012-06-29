package jack.rm.data;

import jack.rm.Main;
import jack.rm.Paths;
import jack.rm.data.set.RomSet;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.*;
import java.io.FileInputStream;

public class Scanner
{
	RomList list;
	boolean scanSubdirectories = true;
	
	public Scanner(RomList list)
	{
		this.list = list;
	}
	
	public long computeCRC(File file)
	{
		try
		{
			CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());

			byte[] buf = new byte[1024];
			
			while (cis.read(buf) >= 0);
			
			return cis.getChecksum().getValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public void scanFolder(File folder) throws Exception
	{
		File[] files = folder.listFiles();
		
		for (int t = 0; t < files.length; ++t)
		{
			if (files[t].isDirectory())
			{
				scanFolder(files[t].getAbsoluteFile());
			}
			if (files[t].getName().endsWith(".zip"))
			{
				Enumeration<? extends ZipEntry> enu = new ZipFile(files[t]).entries();
				String fileName = files[t].getName();
				fileName = fileName.substring(0, fileName.length()-4);
				
				while (enu.hasMoreElements())
				{
					long curCrc = ((ZipEntry)enu.nextElement()).getCrc();
					
					Rom rom = list.getByCRC(curCrc);
					
					if (rom != null)
					{
						if (Renamer.isCorrectlyNamed(fileName, rom))
							rom.status = RomStatus.FOUND;
						else
							rom.status = RomStatus.INCORRECT_NAME;
						
						rom.type = RomType.ZIP;
						rom.path = files[t];
					}
				}
			}
			else if (files[t].getName().endsWith(".gba"))
			{
				long crc = computeCRC(files[t]);
				String fileName = files[t].getName();
				fileName = fileName.substring(0, fileName.length()-4);
				
				Rom rom = list.getByCRC(crc);
				
				if (rom != null)
				{
					if (Renamer.isCorrectlyNamed(fileName, rom))
						rom.status = RomStatus.FOUND;
					else
						rom.status = RomStatus.INCORRECT_NAME;
					
					rom.type = RomType.GBA;
					rom.path = files[t];
				}
			}
		}
	}
	
	public void scanForRoms()
	{
		Main.logln("Scanning for roms in path "+RomSet.current.romPath+"...");
		
		try
		{		
			File folder = new File(RomSet.current.romPath);
			scanFolder(folder);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
