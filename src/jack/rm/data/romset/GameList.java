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

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomID;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.pixbits.lib.io.digest.HashCache;

import jack.rm.Main;
import jack.rm.files.MoverWorker;
import jack.rm.files.RenamerWorker;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

public class GameList implements Iterable<Game>
{
	public final GameSet set;
  List<Game> list;
  HashCache<Rom> cache;
	
	private int countCorrect, countBadlyNamed, countNotFound;
	
	public GameList(GameSet set)
	{
		this.set = set;
	  list = new ArrayList<>();
	}
	
	public void add(Game rom)
	{
		list.add(rom);
	}
	
	public Game get(int i)
	{
		return list.get(i);
	}
	
	public Game getByNumber(int number)
	{
	  for (Game r : list)
	  {
	    int rnumber = r.getAttribute(GameAttribute.NUMBER);
	    if (rnumber == number)
	      return r;
	  }

	  return null;
	}
	
	public int count()
	{
		return list.size();
	}
	
	public void precomputeCache()
	{
		Collections.sort(list);
		cache = new HashCache<>(list.stream().flatMap(g -> g.stream()));
	}
	
	public void clear()
	{
		list.clear();
	}
	
	public HashCache<Rom> getCache() { return cache; }
		
	public Rom getByID(RomID<?> id)
	{
	  return getByCRC32(((RomID.CRC)id).value);
	}
	
	public Rom getByCRC32(long crc)
	{
		return cache.elementForCrc(crc);
	}
	
	public void resetStatus()
	{
		for (Game r : list)
			r.status = GameStatus.MISSING;
		
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
	  
	  for (Game r : list)
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
    for (Game rom : list)
    {
      if (rom.status != GameStatus.MISSING)
      {  
        if (rom.status == GameStatus.FOUND)
        {
          if (!rom.isOrganized())
            rom.status = GameStatus.UNORGANIZED;
        }
        else if (rom.status == GameStatus.UNORGANIZED)
          if (rom.isOrganized())
            rom.status = GameStatus.FOUND;
      }
    }
		
		Main.mainFrame.updateTable();
	}
  
  public Stream<Game> stream() { return list.stream(); }
  public Iterator<Game> iterator() { return list.iterator(); }
  
  public Stream<Rom> romStream() { return list.stream().flatMap(g -> g.stream()); }
  
  public Game find(String search) { 
    Optional<Game> rom = list.stream().filter(set.getSearcher().search(search)).findFirst();
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
