package jack.rm.gui.gamelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.GameStatus;

public class GameListData
{
  private final List<Game> games;
  private final List<Game> sortedGames;
  
  private final boolean[] visibleStatuses = new boolean[GameStatus.values().length];
  
  private Comparator<Game> sorter;
  
  public GameListData()
  {
    Arrays.fill(visibleStatuses, true);
    sorter = null;
    games = new ArrayList<>();
    sortedGames = new ArrayList<>();
  }
  
  public void toggleVisibility(GameStatus status)
  {
    setStatusVisibility(status, !isStatusVisible(status));
  }
  
  protected boolean isStatusVisible(GameStatus status)
  {
    return visibleStatuses[status.ordinal()];
  }
  
  public void setStatusVisibility(GameStatus status, boolean visible)
  {
    this.visibleStatuses[status.ordinal()] = visible;
  }
  
  public boolean isVisible(Game game)
  {
    return visibleStatuses[game.getStatus().ordinal()];
  }
  
  public void setSorter(Comparator<Game> sorter)
  {
    this.sorter = sorter;
  }
  
  public void sort(List<Game> games)
  {
    if (sorter != null)
      games.sort(sorter);
  }
}
