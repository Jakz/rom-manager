package jack.rm.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;

public class RomListModel extends AbstractListModel<Game>
{
	private static final long serialVersionUID = 1L;

	List<Game> list;
	
	boolean isCorrect = true;
	boolean isMissing = true;
	boolean isIncomplete = true;
	boolean isBadlyNamed = true;
	
	public RomListModel()
	{
		list = new ArrayList<>();
	}
	
	public void addElement(Game o)
	{
		Game rom = (Game)o;
		if (isCorrect && rom.getStatus() == GameStatus.FOUND)
			list.add(rom);
		else if (isMissing && rom.getStatus() == GameStatus.MISSING)
			list.add(rom);
		else if (isBadlyNamed && rom.getStatus() == GameStatus.UNORGANIZED)
			list.add(rom);
		else if (isIncomplete && rom.getStatus() == GameStatus.INCOMPLETE)
		  list.add(rom);

		return;
	}
	
	public void removeElement(int index)
	{
		list.remove(index);
	}
	
	@Override
  public Game getElementAt(int index)
	{
		return(list.get(index));
	}
	
	@Override
  public int getSize()
	{
		return(list.size());
	}
	
	public void clear()
	{
		list.clear();
	}
	
	public Consumer<Game> collector() { return list::add; }
	public Stream<Game> stream() { return list.stream(); }
	
	public void fireChanges(int row)
	{
	  this.fireContentsChanged(RomListModel.this, row, row);
	}

	public void fireChanges()
	{
		RomListModel.this.fireContentsChanged(RomListModel.this, 0, list.size());
	}
}