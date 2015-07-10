package jack.rm.data;

import jack.rm.*;
import jack.rm.data.set.RomSet;
import jack.rm.gui.ProgressDialog;

import java.util.*;
import java.io.*;
import java.util.zip.*;

import javax.swing.SwingWorker;

public class RomList
{
	List<Rom> list;
	Map<Long, Rom> crcs;
	
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
	
	public Rom getByNumber(int number)
	{
	  for (Rom r : list)
	    if (r.number == number)
	      return r;
	  
	  return null;
	}
	
	public int count()
	{
		return list.size();
	}
	
	public void sort()
	{
		Collections.sort(list);
	}
	
	public void clear()
	{
		list.clear();
	}
	
	public Rom getByCRC(long crc)
	{
		return crcs.get(crc);
	}
	
	public void resetStatus()
	{
		for (Rom r : list)
			r.status = RomStatus.NOT_FOUND;
		
		countCorrect = 0;
		countBadlyNamed = 0;
		countNotFound = 0;
		countTotal = 0;
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
			String romName = r.title.toLowerCase();
			
			if (!name.equals(""))
			{
  			String[] tokens = name.toLowerCase().split(" ");
  			boolean[] include = new boolean[tokens.length];
  			
  			for (int i = 0; i < tokens.length; ++i)
  			{
  			  include[i] = !(tokens[i].startsWith("-") && tokens[i].length() > 1);
          if (!include[i])
            tokens[i] = tokens[i].substring(1);
  			}
  
  			boolean found = true;
  	    for (int i = 0; i < tokens.length; ++i)
  	    {
  	      if (romName.contains(tokens[i]) != include[i])
  	      {
  	        found = false;
  	        break;
  	      }
  	    }
  
  		  if (!found)
  		    continue;
			}
		  
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
    for (Rom rom : list)
    {
      if (rom.status != RomStatus.NOT_FOUND)
      {  
        String filename = rom.file.file().getName().substring(rom.file.file().getName().length()-4);
        
        if (rom.status == RomStatus.FOUND)
        {
          if (!Renamer.isCorrectlyNamed(filename, rom))
            rom.status = RomStatus.INCORRECT_NAME;
        }
        else if (rom.status == RomStatus.INCORRECT_NAME)
          if (!Renamer.isCorrectlyNamed(filename, rom))
            rom.status = RomStatus.FOUND;
      }
    }
		
		Main.mainFrame.updateTable();
	}
	
	public class RenamerWorker extends SwingWorker<Void, Integer>
  {
    private int total = 0;
    private final RomList list;
    
    RenamerWorker(RomList list)
    {
      this.list = list;
      total = list.count();
    }

    @Override
    public Void doInBackground()
    {
      ProgressDialog.init(Main.mainFrame, "Rom Rename", null);
      
      for (int i = 0; i < list.count(); ++i)
      {
        Rom rom = list.get(i);
        setProgress((int)((((float)i)/total)*100));
        
        if (rom.status == RomStatus.INCORRECT_NAME)
        {        
          Renamer.renameRom(rom);  
          ++Main.romList.countCorrect;
          --Main.romList.countBadlyNamed; 
        }
        
        publish(i);
      }
      
      
      return null;
    }
    
    @Override
    public void process(List<Integer> v)
    {
      ProgressDialog.update(this, "Renaming "+v.get(v.size()-1)+" of "+list.count()+"..");
      Main.mainFrame.updateTable();
    }
    
    @Override
    public void done()
    {
      ProgressDialog.finished();
      
      if (Main.pref.organizeRomsByNumber)
        new OrganizeByFolderWorker(list, 100).execute();
      else
        PersistenceRom.consolidate(list);
    }
    
  }
	
	public class OrganizeByFolderWorker extends SwingWorker<Void, Integer>
  {
    private int total = 0;
    private final RomList list;
    private final int folderSize;
    
    OrganizeByFolderWorker(RomList list, int folderSize)
    {
      this.list = list;
      total = list.count();
      this.folderSize = folderSize;
    }

    @Override
    public Void doInBackground()
    {
      ProgressDialog.init(Main.mainFrame, "Rom Organize", null);
      
      for (int i = 0; i < list.count(); ++i)
      {
        setProgress((int)((((float)i)/total)*100));

        Rom rom = list.get(i); 
        Renamer.organizeRom(rom, folderSize);

        publish(i);
      }
  
      return null;
    }
    
    @Override
    public void process(List<Integer> v)
    {
      ProgressDialog.update(this, "Organizing "+v.get(v.size()-1)+" of "+list.count()+"..");
      Main.mainFrame.updateTable();
    }
    
    @Override
    public void done()
    {
      ProgressDialog.finished();
      PersistenceRom.consolidate(list);
      
      
      //if (Main.pref.organizeRomsDeleteEmptyFolders)
      //  deleteEmptyFolders();
    }
    
  }
	
	
  public class RenameInsizeZipsWorker extends SwingWorker<Void, Integer>
  {
    private int total = 0;
    private final RomList list;
    private final int folderSize;
    
    RenameInsizeZipsWorker(RomList list, int folderSize)
    {
      this.list = list;
      total = list.count();
      this.folderSize = folderSize;
    }

    @Override
    public Void doInBackground()
    {
      ProgressDialog.init(Main.mainFrame, "Rom Zip Renamer", null);
      
      for (int i = 0; i < list.count(); ++i)
      {
        Rom rom = list.get(i);
        
        setProgress((int)((((float)i)/total)*100));

        try {
          if (rom.status != RomStatus.NOT_FOUND && rom.file.type == RomType.ZIP)
          {
            ZipFile zfile = new ZipFile(rom.file.file());
            //ZipFile ffile = new ZipFile()
            
          } 
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        
        publish(i);
      }
  
      return null;
    }
    
    @Override
    public void process(List<Integer> v)
    {
      ProgressDialog.update(this, "Organizing "+v.get(v.size()-1)+" of "+list.count()+"..");
      Main.mainFrame.updateTable();
    }
    
    @Override
    public void done()
    {
      ProgressDialog.finished();
      
      //if (Main.pref.organizeRomsDeleteEmptyFolders)
      //  deleteEmptyFolders();
    }
    
  }
  
	
	public void renameRoms()
	{
		if (!Settings.current().useRenamer)
			return;
		
		new RenamerWorker(this).execute();
	}

	public void deleteEmptyFolders()
	{
		Queue<File> files = new LinkedList<File>();
		files.add(new File(RomSet.current.romPath()));
		
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
