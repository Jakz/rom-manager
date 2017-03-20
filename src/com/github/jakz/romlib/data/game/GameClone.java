package com.github.jakz.romlib.data.game;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public class GameClone<T> implements Iterable<T>
{
  private final T[] games;
  private final T[] zones;
  
  public GameClone(T[] games, T[] zones)
  {
    this.games = games;
    this.zones = zones;
  }

  @SuppressWarnings("unchecked")
  public GameClone(T[] games)
  {
    this.games = games;
    this.zones = (T[])Array.newInstance(games.getClass().getComponentType(), Location.values().length);
  }
  
  public T getBestMatchForBias(BiasSet bias, boolean acceptFallback)
  {
    for (Location location : bias.getLocations())
    {
      if (zones[location.ordinal()] != null)
        return zones[location.ordinal()];
    }
    
    if (acceptFallback)
      return games[0];
    else
      return null;
  }
 
  public T get(Location zone) { return zones[zone.ordinal()]; }
  public T get(int index) { return games[index]; }
  public int size() { return games.length; }
  
  public Iterator<T> iterator() { return Arrays.asList(games).iterator(); }
  public Stream<T> stream() { return Arrays.stream(games); }
}
