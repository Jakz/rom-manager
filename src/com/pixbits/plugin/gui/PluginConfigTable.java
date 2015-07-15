package com.pixbits.plugin.gui;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.pixbits.plugin.Plugin;
import com.pixbits.plugin.PluginArgument;

public class PluginConfigTable extends JTable
{
  private List<Class<?>> types;
  private List<TableCellEditor> editors;
  private List<TableCellRenderer> renderers;
  private final PluginArgumentTableModel model;
  
  class PluginArgumentTableModel extends AbstractTableModel
  {
    private final String[] names = { "Name", "Value" };
    
    List<PluginArgument> arguments = new ArrayList<>();
    
    @Override public int getColumnCount() { return 2; }
    @Override public String getColumnName(int i) { return names[i]; }
    @Override public int getRowCount() { return arguments.size(); }
    @Override public boolean isCellEditable(int r, int c) { return c == 1; }
    
    @Override public Object getValueAt(int r, int c)
    {
      PluginArgument arg = arguments.get(r);
      return c == 0 ? arg.getName() : arg.get();
    }
    
    @Override public void setValueAt(Object v, int r, int c)
    {
      if (v != null)
      {
        PluginArgument arg = arguments.get(r);
        arg.set(v);
      }
    }
  }
  
  public PluginConfigTable()
  {
    super();
    editors = new ArrayList<>();
    renderers = new ArrayList<>();
    
    model = new PluginArgumentTableModel();
    setModel(model);
  }
  
  @Override public TableCellEditor getCellEditor(int r, int c)
  {
    if (c == 0)
      return null;
    else
    {
      return editors.get(r);
    }
  }
  
  @Override public TableCellRenderer getCellRenderer(int r, int c)
  {
    if (c == 0)
      return this.getDefaultRenderer(String.class);
    else
      return renderers.get(r);
  }
  
  @Override public String getToolTipText(MouseEvent e)
  {
    int r = this.rowAtPoint(e.getPoint());
    if (r != -1)
      return model.arguments.get(r).getDescription();
    else
      return null;
  }
  
  public void prepare(Plugin plugin)
  {
    renderers.clear();
    editors.clear();
  
    if (plugin == null)
    {
      model.arguments.clear();
      setEnabled(false);
    }
    else
    {
      setEnabled(true);

      model.arguments = plugin.getArguments();
      
      types = model.arguments.stream().map( a -> a.getType() ).collect(Collectors.toList());
 
      types.stream().map( t -> {
        if (t.equals(Integer.class) || t.equals(Integer.TYPE))
          return new PluginArgumentEditor(t, this.getDefaultEditor(Integer.class));
        else if (t.equals(java.nio.file.Path.class))
          return new PathArgumentEditor();
        else
          return this.getDefaultEditor(Object.class);
      }).forEach(editors::add);
      
      types.stream().map( t -> {
        if (t.equals(Integer.class) || t.equals(Integer.TYPE))
          return this.getDefaultRenderer(Integer.class);
        else
          return this.getDefaultRenderer(Object.class);
      }).forEach(renderers::add);
      
      model.fireTableDataChanged();
    }
  }
}