package jack.rm.gui;

import com.github.jakz.romlib.data.game.Game;

public interface Mediator
{
  public void refreshGameList();
  public void refreshGameList(int row);
  public void refreshGameListCurrentSelection();
  public void rebuildGameList();
  public void setInfoPanelContent(Game game);
  public void selectGameIfVisible(Game game);
}
