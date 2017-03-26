package jack.rm.gui;

import com.github.jakz.romlib.data.game.GameStatus;

public interface Mediator
{
  public void refreshGameList();
  public void refreshGameList(int row);
  public void toggleVisibilityForStatusInGameList(GameStatus status);
  public void rebuildGameList();
}
