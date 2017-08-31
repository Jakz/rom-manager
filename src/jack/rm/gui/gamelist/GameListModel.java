package jack.rm.gui.gamelist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;

public class GameListModel extends AbstractListModel<Game>
{
	private static final long serialVersionUID = 1L;

	List<Game> list;
	final boolean[] visible = new boolean[GameStatus.values().length];
	
	public GameListModel()
	{
		list = new ArrayList<>();
		Arrays.fill(visible, true);
	}
	
	public void addElement(Game o)
	{
		Game rom = (Game)o;
		GameStatus status = rom.getStatus();
		if (visible[status.ordinal()])
		  list.add(rom);
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
	
	public void clear()
	{
		list.clear();
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
	
	public Consumer<Game> collector() { return list::add; }
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