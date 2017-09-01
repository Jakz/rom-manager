package jack.rm.gui.gamelist;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.ui.table.FilterableListDataSource;

public class GameListData
{
  FilterableListDataSource<Game> games;
  
  public GameListData()
  {
    games = new FilterableListDataSource<>();
  }
  
  public Game gameAt(int index) { return games.get(index); }
  private int gameCount() { return games.size(); }
  private Stream<Game> gameStream() { return games.stream(); }
  
  public void setData(List<Game> games) { this.games.setData(games); }
  public void setSorter(Comparator<Game> comparator) { this.games.sort(comparator); }
  public void setFilter(Predicate<Game> filter) { this.games.filter(filter); }
  
  public Stream<Game> stream() { return gameStream(); }
  public int getSize() { return gameCount(); }
}
