package jack.rm.gui;

import jack.rm.Main;
import jack.rm.data.*;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import java.util.*;

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
		if (isCorrect && rom.status == RomStatus.FOUND)
			list.add(rom);
		else if (isMissing && rom.status == RomStatus.NOT_FOUND)
			list.add(rom);
		else if (isBadlyNamed && rom.status == RomStatus.INCORRECT_NAME)
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
	
	public void fireChanges()
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
      public void run() {
				Main.mainFrame.list.clearSelection();
				fireIntervalRemoved(this,0,list.size());
			}
		});
	}
}