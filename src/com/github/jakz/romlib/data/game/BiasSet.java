package com.github.jakz.romlib.data.game;

public class BiasSet
{
  private final Location[] locations;
  
  public BiasSet(Location... zones)
  {
    this.locations = zones;
  }
  
  public Location[] getLocations() { return locations; }
}
