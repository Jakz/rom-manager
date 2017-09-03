package com.github.jakz.romlib.data.set;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.assets.AssetManager;
import com.github.jakz.romlib.data.game.Game;

public class GameSetInfo implements GameSetAttributeInterface
{  
  private final Map<GameSetAttribute, Object> attributes;
  
  private final Provider provider;
  private final DatFormat format;
  private final AssetManager assetManager;
  
  
  private int romCount;
  private int gameCount;
  private int uniqueGameCount;
  private long sizeInBytes;
  
  /*public GameSetInfo(Provider provider)
  {
    this(provider, null);
  }*/
  
  public GameSetInfo(Provider provider, DatFormat format, AssetManager assetManager)
  {
    this.attributes = new HashMap<>();
    this.provider = provider;
    this.format = format;
    this.assetManager = assetManager;
    
    this.setAttribute(GameSetAttribute.CAPTION, provider.getName());
  }
  
  public GameSetInfo(Provider provider)
  {
    this(provider, DatFormat.DUMMY, AssetManager.DUMMY);
  }

  void computeStats(GameSet set)
  {
    this.romCount = (int) set.stream().parallel().map(Game::stream).mapToLong(Stream::count).sum();
    this.gameCount = set.gameCount();
    this.uniqueGameCount = set.clones() != null ? set.clones().size() : set.gameCount();
    this.sizeInBytes = set.stream().parallel().map(Game::stream).map(s -> s.map(r -> r.size()).collect(Collectors.summingLong(Long::longValue))).mapToLong(Long::longValue).sum();

  }
  
  public Provider getProvider() { return provider; }
  public DatFormat getFormat() { return format; }
  public AssetManager getAssetManager() { return assetManager; }
  
  public String getFlavour() { return provider.getFlavour(); }
  
  public int romCount() { return romCount; }
  public int gameCount() { return gameCount; }
  public int uniqueGameCount() { return uniqueGameCount; }
  public long sizeInBytes() { return sizeInBytes; }

  @Override
  public <T> void setAttribute(GameSetAttribute attrib, T value)
  {
    attributes.put(attrib, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getAttribute(GameSetAttribute attrib)
  {
    return (T) attributes.get(attrib);
  }
}
