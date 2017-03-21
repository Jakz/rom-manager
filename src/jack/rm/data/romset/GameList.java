package jack.rm.data.romset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.io.digest.HashCache;

public class GameList implements Iterable<Game>
{
	private final GameSetStatus status;
  private final Game[] games;
  private final HashCache<Game> cache;
  private final HashMap<String, Game> nameMap;
		
  public GameList(List<Game> games)
  {
    this(games.toArray(new Game[games.size()]));
  }
  
	public GameList(Game[] games)
	{
	  this.games = games;
	  status = new GameSetStatus();
	  Arrays.sort(games);
	  cache = new HashCache<>(Arrays.asList(games));
	  
	  nameMap = stream().collect(Collectors.toMap(
	    g -> g.getTitle(), 
	    g -> g, 
	    (v1, v2) -> v1, 
	    () -> new HashMap<>()
	  ));
	}

	public Game get(String title) { return nameMap.get(title); }
	public Game get(int i) { return games[i]; }
	
	public GameSetStatus status() { return status; }
	public int gameCount() { return games.length; }

	public HashCache<Game> cache() { return cache; }
		
	public Game getByID(GameID<?> id)
	{
	  return getByCRC32(((GameID.CRC)id).value);
	}
	
	public Game getByCRC32(long crc)
	{
		return cache.elementForCrc(crc);
	}
	

	public void resetStatus()
	{
		for (Game r : games)
			r.status = GameStatus.MISSING;
	}
	
	public void refreshStatus()
	{
    status.refresh(stream());
	}
	
	public void checkNames()
	{
    for (Game rom : games)
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
	}
  
  public Stream<Game> stream() { return Arrays.stream(games); }
  public Iterator<Game> iterator() { return Arrays.asList(games).iterator(); }
}
