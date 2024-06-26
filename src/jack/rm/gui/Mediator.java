package jack.rm.gui;

import com.github.jakz.romlib.data.game.Game;

import jack.rm.gui.gamelist.GameListData;

public interface Mediator
{
  public void repaint();
  
  public void refreshGameList();
  public void refreshGameList(int row);
  public void refreshGameListCurrentSelection();
  public void refreshGameListCounters();
  public void rebuildGameList();
  public void switchGameListMode(GameListData.Mode mode, boolean treeMode);
  
  public void refreshGameStatusCheckboxesInViewMenu();
  
  public void setInfoPanelContent(Game game);
  public void selectGameIfVisible(Game game);
  
  public UIPreferences preferences();
}
