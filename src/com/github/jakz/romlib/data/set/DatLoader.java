package com.github.jakz.romlib.data.set;

public interface DatLoader
{
  public class Data
  {
    public final GameList games;
    public final CloneSet clones;
    
    public Data(GameList games)
    {
      this.games = games;
      this.clones = null;
    }
    
    public Data(GameList games, CloneSet clones)
    {
      this.games = games;
      this.clones = clones;
    }
  
  }
  
  Data load(GameSet set);
  DatFormat getFormat();
  
  public static DatLoader build(final DatFormat format)
  {
    return new DatLoader()
    {
       @Override public Data load(GameSet set) { throw new UnsupportedOperationException("DatLoader for " + format + " doesn't have a loader."); }
       @Override public DatFormat getFormat() { return format; }
    };
  }
}
