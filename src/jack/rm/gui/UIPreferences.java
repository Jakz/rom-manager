package jack.rm.gui;

import java.util.EnumMap;
import java.util.Map;

import com.github.jakz.romlib.data.game.GameStatus;

import jack.rm.gui.gamelist.GameListData;

public class UIPreferences
{
  private final EnumMap<GameStatus, Boolean> statusVisibility = new EnumMap<>(GameStatus.class); 

  public UIPreferences()
  {
    for (GameStatus status : GameStatus.values())
      statusVisibility.put(status, true);
  }
  
  public GameListData.Mode gameListViewMode = GameListData.Mode.GAMES;
  public boolean gameListTreeMode = false;
  public boolean showTotalsInCountPanel = false;
  
  public void setStatusVisibility(GameStatus status, boolean visible) { statusVisibility.put(status, visible); }
  public boolean isStatusVisibile(GameStatus status) { return statusVisibility.getOrDefault(status, true); }
}
