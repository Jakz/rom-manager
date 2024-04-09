package jack.rm.gui.gamelist;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Drawable;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.pixbits.lib.ui.table.FilterableListDataSource;

public class GameListData
{
  public static enum Mode
  {
    GAMES, CLONES
  };

  private FilterableListDataSource<Game> games;
  private FilterableListDataSource<GameClone> clones;
  private Mode mode;

  public GameListData(Mode mode)
  {
    games = new FilterableListDataSource<>();
    clones = new FilterableListDataSource<>();
    this.mode = mode;
  }

  public Mode getMode()
  {
    return mode;
  }

  public void setMode(Mode mode)
  {
    this.mode = mode;
  }

  private GameClone cloneAt(int index)
  {
    return clones.get(index);
  }

  private int cloneCount()
  {
    return clones.size();
  }

  private Game gameAt(int index)
  {
    return games.get(index);
  }

  private int gameCount()
  {
    return games.size();
  }

  private Stream<GameClone> cloneStream()
  {
    return clones.stream();
  }

  private Stream<Game> gameStream()
  {
    return games.stream();
  }

  public void setData(List<Game> games, List<GameClone> clones)
  {
    this.games.setData(games);
    this.clones.setData(clones);
  }

  public void setSorter(Comparator<? super Drawable> comparator)
  {
    this.games.sort(comparator);
    this.clones.sort(comparator);
  }

  public void setFilter(Predicate<Game> filter)
  {
    this.games.filter(filter);
    this.clones.filter(gc -> gc.stream().anyMatch(filter));
  }

  public Drawable get(int index)
  {
    return mode == Mode.GAMES ? gameAt(index) : cloneAt(index);
  }

  public Stream<Drawable> stream()
  {
    return mode == Mode.GAMES ? gameStream().map(c -> (Drawable) c) : cloneStream().map(c -> (Drawable) c);
  }

  public Stream<Drawable> originalStream()
  {
    return mode == Mode.GAMES ? games.originalStream().map(c -> (Drawable) c)
        : clones.originalStream().map(c -> (Drawable) c);
  }

  public int getSize()
  {
    return mode == Mode.GAMES ? gameCount() : cloneCount();
  }
}
