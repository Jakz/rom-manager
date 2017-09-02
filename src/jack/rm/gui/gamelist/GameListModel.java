package jack.rm.gui.gamelist;

import javax.swing.AbstractListModel;

import com.github.jakz.romlib.data.game.Game;

public class GameListModel extends AbstractListModel<Game>
{
	private static final long serialVersionUID = 1L;
	
	private final GameListData data;
	
	public GameListModel(GameListData data)
	{
		this.data = data;
	}

	@Override
  public Game getElementAt(int index)
	{
		return data.gameAt(index);
	}
	
	@Override
  public int getSize()
	{
		return data.getSize();
	}

	public void fireChanges(int row)
	{
	  this.fireContentsChanged(GameListModel.this, row, row);
	}

	public void fireChanges()
	{
		GameListModel.this.fireContentsChanged(GameListModel.this, 0, getSize());
	}
}