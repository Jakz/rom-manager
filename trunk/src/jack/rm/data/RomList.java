package jack.rm.data;

import jack.rm.*;
import jack.rm.gui.*;
import jack.rm.i18n.Text;

import java.util.*;
import java.io.*;
import java.util.zip.*;

public class RomList
{
	List<Rom> list;
	HashMap<Long, Rom> crcs;
	
	public int countCorrect, countBadlyNamed, countNotFound, countTotal;
	
	public RomList()
	{
		list = new ArrayList<Rom>();
		crcs = new HashMap<Long, Rom>();
	}
	
	public void add(Rom rom)
	{
		list.add(rom);
		crcs.put(rom.crc,rom);
	}
	
	public Rom get(int i)
	{
		return list.get(i);
	}
	
	public int count()
	{
		return list.size();
	}
	
	public void sort()
	{
		Collections.sort(list);
	}
	
	public Rom getByCRC(long crc)
	{
		return crcs.get(crc);
	}
	
	public void search(String name, RomSize size, Location loc, Language lang)
	{
		Main.mainFrame.romListModel.clear();
		countCorrect = 0;
		countBadlyNamed = 0;
		countNotFound = 0;
		countTotal = 0;

		for (int t = 0; t < list.size(); ++t)
		{
			Rom r = list.get(t);
			
			if (name == "" || r.title.toLowerCase().contains(name))
			{
				if (size == null || r.size == size)
				{
					if (loc == null || r.location == loc)
					{
						if (lang == null || (r.languages & lang.code) != 0)
						{
							Main.mainFrame.romListModel.addElement(r);
						}
					}
				}
			}

			switch (r.status)
			{
				case FOUND: ++countCorrect; break;
				case INCORRECT_NAME: ++countBadlyNamed; break;
				case NOT_FOUND: ++countNotFound; break;
			}	
			++countTotal;
		}
		
		Main.mainFrame.updateTable();
	}
	
	public void showAll()
	{
		search("", null, null, null);
	}
	
	public void checkNames()
	{
		for (int x = 0; x < list.size(); ++x)
		{
			Rom rom = list.get(x);
			
			if (rom.status != RomStatus.NOT_FOUND)
			{				
				if (rom.path.getName().startsWith(Renamer.getCorrectName(rom)))
					rom.status = RomStatus.FOUND;
				else
					rom.status = RomStatus.INCORRECT_NAME;
			}
		}
		
		Main.mainFrame.updateTable();
	}
	
	public void renameRoms()
	{
		for (int x = 0; x < list.size(); ++x)
		{
			Rom rom = list.get(x);
			
			if (rom.status == RomStatus.INCORRECT_NAME)
			{				
				String renameTo = rom.path.getParent()+File.separator+Renamer.getCorrectName(rom)+"."+rom.type.ext;
								
				File tmp = rom.path;
				
				File newF = new File(renameTo);
				while (!tmp.renameTo(newF));
				
				rom.status = RomStatus.FOUND;
				rom.path = newF;
			}
		}
	}
	
	public void organizeRomsByNumber()
	{
		int folderSize = 100;
		
		for (int x = 0; x < list.size(); ++x)
		{
			Rom rom = list.get(x);
			
			if (rom.status != RomStatus.NOT_FOUND)
			{
				int which = (rom.number - 1) / folderSize;
				
				String first = Renamer.formatNumber(folderSize*which+1);
				String last = Renamer.formatNumber(folderSize*(which+1));
				
				String finalPath = RomSet.current.romPath+first+"-"+last+File.separator;
				
				System.out.println("Creating "+finalPath);
				new File(finalPath).mkdirs();
				
				File newFile = new File(finalPath+rom.path.getName());
				
				if (!newFile.equals(rom.path))
				{
					Main.logln("Moving rom "+Renamer.formatNumber(rom.number)+" to "+finalPath);
					while (!rom.path.renameTo(newFile));
					rom.path = newFile;
				}
			}			
		}
	}
	
	public void deleteEmptyFolders()
	{
		Queue<File> files = new LinkedList<File>();
		files.add(new File(RomSet.current.romPath));
		
		while (!files.isEmpty())
		{
			File f = files.poll();
			File[] l = f.listFiles();
			
			for (File ff : l)
			{
				if (ff.isDirectory())
				{
					if (ff.listFiles().length == 0)
						ff.delete();
					else
						files.add(ff);
				}
			}
		}
	}
}
