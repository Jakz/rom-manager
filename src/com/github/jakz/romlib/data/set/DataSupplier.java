package com.github.jakz.romlib.data.set;

import java.util.Optional;

import com.github.jakz.romlib.data.game.Game;

public interface DataSupplier
{
  public class Data
  {
    public final Optional<GameList> games;
    public final Optional<CloneSet> clones;
    public final Optional<GameSetInfo> setInfo;
    
    public Data(GameList games)
    {
      this.games = Optional.of(games);
      this.clones = Optional.empty();
      this.setInfo = Optional.empty();
    }
    
    public Data(GameList games, CloneSet clones)
    {
      this.games = Optional.of(games);
      this.clones = Optional.of(clones);
      this.setInfo = Optional.empty();
    }
  
  }
  
  Data load(GameSet set);
  DatFormat getFormat();
  
  public static DataSupplier build(final DatFormat format)
  {
    return new DataSupplier()
    {
       @Override public Data load(GameSet set) { throw new UnsupportedOperationException("DatLoader for " + format + " doesn't have a loader."); }
       @Override public DatFormat getFormat() { return format; }
    };
  }
  
  public static DataSupplier build(final GameList gameList, final CloneSet clones)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return new Data(gameList, clones); }
      @Override public DatFormat getFormat() { return DatFormat.DUMMY; }
    };
  }
  
  public static DataSupplier build(final GameList gameList)
  {
    return new DataSupplier()
    {
      @Override public Data load(GameSet set) { return new Data(gameList); }
      @Override public DatFormat getFormat() { return DatFormat.DUMMY; }
    };
  }
}
