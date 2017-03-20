package jack.rm.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.swing.AbstractListModel;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.GameStatus;

public class RomListModel extends AbstractListModel<Rom>
{
	private static final long serialVersionUID = 1L;

	List<Rom> list;
	
	boolean isCorrect = true;
	boolean isMissing = true;
	boolean isBadlyNamed = true;
	
	public RomListModel()
	{
		list = new ArrayList<Rom>();
	}
	
	public void addElement(Object o)
	{
		Rom rom = (Rom)o;
		if (isCorrect && rom.status == GameStatus.FOUND)
			list.add(rom);
		else if (isMissing && rom.status == GameStatus.MISSING)
			list.add(rom);
		else if (isBadlyNamed && rom.status == GameStatus.UNORGANIZED)
			list.add(rom);

		return;
	}
	
	public void removeElement(int index)
	{
		list.remove(index);
	}
	
	@Override
  public Rom getElementAt(int index)
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
	
	public Consumer<Rom> collector() { return list::add; }
	public Stream<Rom> stream() { return list.stream(); }
	
	public void fireChanges(int row)
	{
	  this.fireContentsChanged(RomListModel.this, row, row);
	}

	public void fireChanges()
	{
		RomListModel.this.fireContentsChanged(RomListModel.this, 0, list.size());
	}
}