package jack.rm.data;

import jack.rm.*;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;
import jack.rm.files.MoverWorker;
import jack.rm.files.*;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RomList
{
	public final RomSet set;
  List<Rom> list;
	Map<Long, Rom> crcs;
	
	private int countCorrect, countBadlyNamed, countNotFound, countTotal;
	
	public RomList(RomSet set)
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
	  {
	    int rnumber = r.getAttribute(RomAttribute.NUMBER);
	    if (rnumber == number)
	      return r;
	  }

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
			r.status = RomStatus.MISSING;
		
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
	      case MISSING: ++countNotFound; break;
	      case UNORGANIZED: ++countBadlyNamed; break;
	      case FOUND: ++countCorrect; break;
	    }
	  }
	}

	public void checkNames()
	{
    for (Rom rom : list)
    {
      if (rom.status != RomStatus.MISSING)
      {  
        if (rom.status == RomStatus.FOUND)
        {
          if (!rom.isOrganized())
            rom.status = RomStatus.UNORGANIZED;
        }
        else if (rom.status == RomStatus.UNORGANIZED)
          if (rom.isOrganized())
            rom.status = RomStatus.FOUND;
      }
    }
		
		Main.mainFrame.updateTable();
	}
  
  public Stream<Rom> stream() { return list.stream(); }
  
  public void organize()
  {
    RenamerPlugin renamer = set.getSettings().getRenamer();
    FolderPlugin organizer = set.getSettings().getFolderOrganizer();
    boolean hasCleanupPhase = set.getSettings().hasCleanupPlugins();
    
    Consumer<Boolean> cleanupPhase = b -> { set.cleanup(); set.saveStatus(); };
    Consumer<Boolean> moverPhase = organizer == null ? cleanupPhase : b -> new MoverWorker(set, organizer, cleanupPhase).execute();
    Consumer<Boolean> renamerPhase = renamer == null ? moverPhase : b -> new RenamerWorker(set, renamer, moverPhase).execute();

    renamerPhase.accept(true);
  }  
}
