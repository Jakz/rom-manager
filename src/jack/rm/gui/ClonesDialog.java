package jack.rm.gui;

import jack.rm.Settings;
import jack.rm.data.*;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.util.*;
import java.util.stream.Collectors;

import jack.rm.data.ScanResult;

public class ClonesDialog extends JDialog
{
  final JTable table;
  final CloneTableModel model;
  
  List<ScanResult> clones = new ArrayList<>(); 
  Map<ScanResult, Boolean> keep = new HashMap<>();
  Map<Rom, Color> colors = new HashMap<>();
  
  private class CloneTableModel extends AbstractTableModel
  {
    String[] columnNames = new String[] { "Keep", "Rom", "Path" };
    Class<?>[] columnClasses = new Class<?>[] { Boolean.class, String.class, String.class };

    @Override public int getColumnCount() { return columnClasses.length; }
    @Override public String getColumnName(int i) { return columnNames[i]; }
    @Override public Class<?> getColumnClass(int i) { return columnClasses[i]; }
    @Override public int getRowCount() { return clones.size(); }
    
    @Override public Object getValueAt(int r, int c)
    {
      switch (c)
      {
        case 0: return keep.get(clones.get(r));
        case 1: return clones.get(r).rom;
        case 2: return Settings.current().romsPath.relativize(clones.get(r).entry.file());
        default: return null;
      }
    }
    
    @Override public boolean isCellEditable(int r, int c) { return c == 0; }
    
    @Override public void setValueAt(Object value, int r, int col)
    {
      Boolean bool = (Boolean)value;
      ScanResult result = clones.get(r);
      
      keep.put(clones.get(r), (Boolean)value);
      
      clones.stream().filter( c -> c.rom == result.rom && c != result ).forEach( c -> keep.put(c, false) );
      fireChanges();
    }
    
    public void fireChanges()
    {
      this.fireTableDataChanged();
    }
    
    class BooleanTableCellRenderer extends JCheckBox implements TableCellRenderer
    {

      public BooleanTableCellRenderer()
      {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
      }

      @Override
      public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {  
        if (isSelected) 
        {
          setForeground(table.getSelectionForeground());
          setBackground(table.getSelectionBackground());
        } 
        else
        {
          setForeground(table.getForeground());
          setBackground(table.getBackground());
        }
      
        setSelected((value != null && ((Boolean)value).booleanValue())); return this; 
      }
      @Override public boolean isOpaque() { return true; }
    }
    
    
    class Renderer implements TableCellRenderer
    {
      private TableCellRenderer inner;
      
      Renderer(TableCellRenderer inner)
      {
        this.inner = inner;
      }
      
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c)
      {
        JComponent label = (JComponent)(inner.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c));
        label.setOpaque(true);
        label.setBackground(colors.get(clones.get(r).rom));        
        return label;
      }
    }
  }
  
  public ClonesDialog(Frame frame, String title)
  {
    super(frame, title);
    
    model = new CloneTableModel();
    table = new JTable(model);
    JScrollPane pane = new JScrollPane(table);
    
    table.setDefaultRenderer(Boolean.class, model.new Renderer(model.new BooleanTableCellRenderer()));
    table.setDefaultRenderer(String.class, model.new Renderer(new DefaultTableCellRenderer()));
    
    //pane.setPreferredSize(new Dimension());
    
    this.add(pane);
    
    pack();
  }
  
  public void activate(RomList roms, Set<ScanResult> clones)
  {
    this.keep.clear();
    this.clones.clear();
    
    Set<Rom> romClones = clones.stream().map( c -> c.rom ).collect(Collectors.toSet());
   
    this.clones = new ArrayList<>(clones);
    this.clones.addAll(roms.stream()
        .filter( r -> romClones.contains(r))
        .map( r -> new ScanResult(r, r.entry) )
        .collect(Collectors.toList()));
    
    Collections.sort(this.clones);
    this.keep = this.clones.stream().collect(Collectors.toMap( c -> c, c -> false));
    this.colors = romClones.stream().collect(Collectors.toMap( c -> c, c -> GUI.randomColor()));
    
    model.fireChanges();
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }
}
