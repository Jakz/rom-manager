package jack.rm.data.romset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jack.rm.Main;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomGroup;
import jack.rm.data.rom.RomGroupID;
import jack.rm.data.rom.RomID;
import jack.rm.data.rom.RomStatus;
import jack.rm.files.MoverWorker;
import jack.rm.files.RenamerWorker;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

public class RomList implements Iterable<Rom>, RomHashFinder
{
	public final RomSet set;
  List<Rom> list;
  Map<RomGroupID, RomGroup> groups;
	Map<Long, Rom> crcs;
	
	private int countCorrect, countBadlyNamed, countNotFound;
	
	public RomList(RomSet set)
	{
		this.set = set;
	  list = new ArrayList<>();
	  groups = new HashMap<>();
		crcs = new HashMap<>();
	}
	
	public void add(Rom rom)
	{
		list.add(rom);
		crcs.put(rom.getCRC(),rom);
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
	  return getByCRC32(((RomID.CRC)id).value);
	}
	
	@Override public Rom getByCRC32(long crc)
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
  public Iterator<Rom> iterator() { return list.iterator(); }
  
  public int groupsCount() { return groups.size(); }
  public Stream<RomGroup> groupsStream() { return groups.values().stream(); }
  public Iterator<RomGroup> groupsIterator() { return groups.values().iterator(); }
  
  public RomGroup getGroup(RomGroupID ident)
  {
    return groups.computeIfAbsent(ident, i -> new RomGroup(i));
  }
  
  public Rom find(String search) { 
    Optional<Rom> rom = list.stream().filter(set.getSearcher().search(search)).findFirst();
    if (!rom.isPresent()) throw new RuntimeException("RomList::find failed to find any rom");
    return rom.orElse(null);
  }
  
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
