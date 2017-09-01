package jack.rm.gui.gamelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.ui.table.FilterableListDataSource;

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