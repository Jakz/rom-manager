package jack.rm.gui;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.Frame;
import java.util.*;

import jack.rm.data.ScanResult;

public class ClonesDialog extends JDialog
{
  JTable table;
  
  final List<ScanResult> clones = new ArrayList<>(); 
  final Map<ScanResult, Boolean> keep = new HashMap<>();
  
  private class CloneTableModel extends AbstractTableModel
  {
    String[] columnNames = new String[] { "Keep", "Rom", "Path" };
    Class<?>[] columnClasses = new Class<?>[] { Boolean.class, String.class, String.class };

    @Override public int getColumnCount() { return columnClasses.length; }
    @Override public String getColumnName(int i) { return columnNames[i]; }
    @Override public int getRowCount() { return clones.size(); }
    
    @Override public Object getValueAt(int r, int c)
    {
      switch (c)
      {
        case 0: return keep.get(clones.get(r));
        case 1: return clones.get(r).rom;
        case 2: return clones.get(r).entry.file();
        default: return null;
      }
    }
    
    @Override public boolean isCellEditable(int r, int c) { return c == 0; }
    
    @Override public void setValueAt(Object value, int r, int c)
    {
      keep.put(clones.get(r), (Boolean)value);
    }
    
    
  }
  
  public ClonesDialog(Frame frame, String title, Callback cb)
  {
    super(frame, title);
  }
}
