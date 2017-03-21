package jack.rm.data.romset;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DatLoader;
import com.github.jakz.romlib.data.set.Provider;

public class GameSetInfo
{
  private final Provider provider;
  private final DatLoader datLoader;
  
  private int romCount;
  private int gameCount;
  private int uniqueGameCount;
  private long sizeInBytes;
  
  /*public GameSetInfo(Provider provider)
  {
    this(provider, null);
  }*/
  
  public GameSetInfo(Provider provider, DatLoader loader)
  {
    this.provider = provider;
    this.datLoader = loader;
  }

  void computeStats(GameSet set)
  {
    this.romCount = (int) set.stream().parallel().map(Game::stream).mapToLong(Stream::count).sum();
    this.gameCount = set.gameCount();
    this.uniqueGameCount = set.clones() != null ? set.clones().size() : set.gameCount();
    this.sizeInBytes = set.stream().parallel().map(Game::stream).map(s -> s.map(r -> r.size()).collect(Collectors.summingLong(Long::longValue))).mapToLong(Long::longValue).sum();

  }
  
  public Provider getProvider() { return provider; }
  public DatFormat getFormat() { return datLoader.getFormat(); }
  public DatLoader getLoader() { return datLoader; }
  
  public String getName() { return provider.getName(); }
  public String getAuthor() { return provider.getAuthor(); }
  public String getDescription() { return provider.getDescription(); }
  public String getComment() { return provider.getComment(); }
  public String getVersion() { return provider.getVersion(); }
  public String getFlavour() { return provider.getFlavour(); }
  
  public int romCount() { return romCount; }
  public int gameCount() { return gameCount; }
  public int uniqueGameCount() { return uniqueGameCount; }
  public long sizeInBytes() { return sizeInBytes; }
}
