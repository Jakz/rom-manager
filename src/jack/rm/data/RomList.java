package jack.rm.data;

import jack.rm.*;
import jack.rm.data.set.RomSet;
import jack.rm.files.MoverWorker;
import jack.rm.files.Organizer;
import jack.rm.plugins.folder.FolderPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.*;
import javax.swing.SwingWorker;

import com.pixbits.gui.ProgressDialog;

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
		
	public Rom getByID(RomID<?> id)
	{
	  return getByCRC(((RomID.CRC)id).value);
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
          set.saveStatus();
        };
        
        new MoverWorker(RomList.this, plugin, callback).execute();
      }
      else
        set.saveStatus();
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
          if (rom.status != RomStatus.NOT_FOUND && rom.getPath().isArchive())
          {
            ZipFile zfile = new ZipFile(rom.getPath().file().toFile());
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
