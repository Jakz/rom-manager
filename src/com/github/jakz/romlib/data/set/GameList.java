package com.github.jakz.romlib.data.set;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.Rom;
import com.pixbits.lib.io.digest.HashCache;

public class GameList implements Iterable<Game>
{
  private final GameSetStatus status;
  private final Game[] games;
  private final HashCache<Rom> cache;
  private final HashMap<String, Game> nameMap;
  private final boolean hasMultipleRomsPerGame;
  
  public GameList(List<Game> games)
  {
    this(games.toArray(new Game[games.size()]));
  }
  
	public GameList(Game[] games)
	{
	  this.games = games;
	  status = new GameSetStatus();
	  Arrays.sort(games);
	  cache = new HashCache<>(Arrays.stream(games).flatMap(g -> g.stream()));
	  
	  nameMap = stream().collect(Collectors.toMap(
	    g -> g.getTitle(), 
	    g -> g, 
	    (v1, v2) -> v1, 
	    () -> new HashMap<>()
	  ));
	  
	  hasMultipleRomsPerGame = stream().anyMatch(g -> g.stream().count() > 1);
	}

	public Game get(String title) { return nameMap.get(title); }
	public Game get(int i) { return games[i]; }
	
	public GameSetStatus status() { return status; }
	public int gameCount() { return games.length; }

	public HashCache<Rom> cache() { return cache; }

	public void resetStatus()
	{
		Arrays.stream(games).forEach(g -> {
		  g.setStatus(GameStatus.MISSING);
		  g.stream().forEach(r -> r.setHandle(null));
		});
	}
	
	public void refreshStatus()
	{
    status.refresh(stream());
	}
	
	public void checkNames()
	{
    Arrays.stream(games).forEach(Game::updateStatus);
	}
  
  public Stream<Game> stream() { return Arrays.stream(games); }
  public Iterator<Game> iterator() { return Arrays.asList(games).iterator(); }
  
  public boolean hasMultipleRomsPerGame() { return hasMultipleRomsPerGame; }
}
