package com.github.jakz.romlib.data.game;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GameClone implements Iterable<Game>
{  
  private final Game[] games;
  
  private final String[] names;
  //private final Game[] zones;
  
  /*public GameClone(Game game, Location zone)
  {
    this.games = new Game[] { game };
    this.zones = new Game[Location.values().length];
    this.zones[zone.ordinal()] = game;
    this.names = null;
  }
  
  public GameClone(Collection<Game> games, Game[] zones)
  {
    this.games = games.toArray(new Game[games.size()]);
    this.zones = zones;
    this.names = null;
  }*/
  
  public GameClone(Game game, Location location, String name)
  {
    this.games = new Game[] { game };
    // TODO: maybe better management without the need of the whole array
    this.names = new String[Location.values().length];
    this.names[location.ordinal()] = name;
  }
  
  public GameClone(Collection<Game> games, String[] names)
  {
    this.games = games.toArray(new Game[games.size()]);
    this.names = names;
  }

  public GameClone(Collection<Game> games)
  {
    this(games, null);
  }
  
  public String getTitleForBias(BiasSet bias, boolean acceptFallback)
  {
    if (names != null)
    {
      for (Location location : bias.getLocations())
        if (names[location.ordinal()] != null)
          return names[location.ordinal()];
      
      /* TODO: should check for a contained bias, eg: Germany is contained in Europe */
      
      return games[0].getTitle();
    }
    else
    {
      Game game = getBestMatchForBias(bias, acceptFallback);
      return game != null ? game.getTitle() : null;
    }
  }
   
  public Game getBestMatchForBias(BiasSet bias, boolean acceptFallback)
  {    
    for (Location location : bias.getLocations())
    {
      Optional<Game> game = Arrays.stream(games).filter(g -> g.getLocation().is(location)).findAny();
      if (game.isPresent())
        return game.get();
    }

    if (acceptFallback)
      return games[0];
    else
      return null;
  }
 
  public Game get(int index) { return games[index]; }
  public int size() { return games.length; }
  
  public Iterator<Game> iterator() { return Arrays.asList(games).iterator(); }
  public Stream<Game> stream() { return Arrays.stream(games); }
}
