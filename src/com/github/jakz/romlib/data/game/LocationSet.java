package com.github.jakz.romlib.data.game;

import java.util.Arrays;
import java.util.List;

import com.github.jakz.romlib.ui.Icon;

public class LocationSet
{
  private long mask;
  
  public LocationSet(Location... locations)
  {
    mask = Arrays.stream(locations).reduce(0L, (m,l) -> m | l.mask, (u,v) -> u | v);
  }
  
  public LocationSet(long mask)
  {
    this.mask = mask;
  }
  
  public void add(Location location) { mask |= location.mask; }
  public boolean is(Location location) { return (mask & location.mask) != 0; }
  public boolean isJust(Location location) { return mask == location.mask; }
  public boolean isLocalized() { return mask != 0; }
  
  public long getMask() { return mask; }
  
  public Location getExactLocation()
  {
    Location location = Location.getExactLocation(this);

    if (location != Location.NONE)
      return location;
    else
      return Location.NONE;
  }
  
  public Location mostCompatibleLocation()
  {
    Location location = getExactLocation();
    
    if (location != Location.NONE)
      return location;
    else
    {
      List<Location> locations = Location.getFuzzyLocations(this);
      return locations.stream().findAny().orElse(Location.NONE);
    }
  }
  
  public Icon getIcon()
  {
    Location location = Location.getExactLocation(this);
    
    if (location != Location.NONE)
      return location.icon;
    else
      return null;
  }
}
