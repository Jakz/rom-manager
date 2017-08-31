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
	
	private FilterableListDataSource<Game> list;
	private final boolean[] visible = new boolean[GameStatus.values().length];
	
	public GameListModel()
	{
		list = new FilterableListDataSource<>();
		Arrays.fill(visible, true);
	}

	@Override
  public Game getElementAt(int index)
	{
		return list.get(index);
	}
	
	@Override
  public int getSize()
	{
		return list.size();
	}
	
	public boolean isVisible(GameStatus status)
	{
	  return visible[status.ordinal()];
	}
	
	public void setVisibility(GameStatus status, boolean visible)
	{
	  this.visible[status.ordinal()] = visible;
	}
	
	public void toggleVisibility(GameStatus status)
	{
	  setVisibility(status, !isVisible(status));
	}
	
	public void setData(List<Game> data) { list.setData(data); }
	public void setFilter(Predicate<Game> filter) { list.filter(filter); }
	public void setSorter(Comparator<Game> sorter) { list.sort(sorter); }
	
	public Stream<Game> stream() { return list.stream(); }
	
	public void fireChanges(int row)
	{
	  this.fireContentsChanged(GameListModel.this, row, row);
	}

	public void fireChanges()
	{
		GameListModel.this.fireContentsChanged(GameListModel.this, 0, list.size());
	}
}