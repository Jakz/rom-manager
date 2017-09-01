package jack.rm.gui;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;

public interface Mediator
{
  public void refreshGameList();
  public void refreshGameList(int row);
  public void refreshGameListCurrentSelection();
  public void rebuildGameList();
  public void setInfoPanelContent(Game game);
}
