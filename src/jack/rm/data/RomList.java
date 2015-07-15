package jack.rm.data;

import jack.rm.*;
import jack.rm.data.set.RomSet;
import jack.rm.files.OrganizerWorker;
import jack.rm.files.MoverWorker;
import jack.rm.files.Organizer;
import jack.rm.gui.ProgressDialog;
import jack.rm.plugins.folder.FolderPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.*;
import javax.swing.SwingWorker;

public class RomList
{
	public final RomSet<? extends Rom> set;
  List<Rom> list;
	Map<Long, Rom> crcs;
	
	private int countCorrect, countBadlyNamed, countNotFound, countTotal;
	
	public RomList(RomSet<? extends Rom> set)
	{
		this.set = set;
	  list = new ArrayList<>();
		crcs = new HashMap<>();
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
	    if (((NumberedRom)r).number == number)
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
		
		updateStatus();
	}
	
	public int getCountCorrect() { return countCorrect; }
	public int getCountMissing() { return countNotFound; }
	public int getCountBadName() { return countBadlyNamed; }
	
	public void updateStatus()
	{
	  countTotal = list.size();
	  countNotFound = 0;
	  countBadlyNamed = 0;
	  countCorrect = 0;
	  
	  for (Rom r : list)
	  {
	    switch (r.status)
	    {
	      case NOT_FOUND: ++countNotFound; break;
	      case INCORRECT_NAME: ++countBadlyNamed; break;
	      case FOUND: ++countCorrect; break;
	    }
	  }
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
        if (rom.status == RomStatus.FOUND)
        {
          if (!rom.hasCorrectName())
            rom.status = RomStatus.INCORRECT_NAME;
        }
        else if (rom.status == RomStatus.INCORRECT_NAME)
          if (rom.hasCorrectName())
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
          Organizer.renameRom(rom);  
          ++RomList.this.countCorrect;
          --RomList.this.countBadlyNamed; 
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
      
      FolderPlugin plugin = set.getSettings().getFolderOrganizer();
      
      if (plugin != null)
      {
        Consumer<Boolean> callback = b -> {
          set.cleanup();
          RomJsonState.consolidate(list);
        };
        
        new MoverWorker(RomList.this, plugin, callback).execute();
      }
      else
        RomJsonState.consolidate(list);
    }
    
  }
	
	public class RenameInsizeZipsWorker extends SwingWorker<Void, Integer>
  {
    private int total = 0;
    private final RomList list;
    
    RenameInsizeZipsWorker(RomList list)
    {
      this.list = list;
      total = list.count();
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
          if (rom.status != RomStatus.NOT_FOUND && rom.entry.type == RomType.ZIP)
          {
            ZipFile zfile = new ZipFile(rom.entry.file().toFile());
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
  
  public Stream<Rom> stream() { return list.stream(); }
  
	
	public void renameRoms()
	{
		new RenamerWorker(this).execute();
	}
}
