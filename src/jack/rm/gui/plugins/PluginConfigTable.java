package jack.rm.gui.plugins;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jack.rm.plugin.Plugin;
import jack.rm.plugin.PluginArgument;

class PluginConfigTable extends JTable
{
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
  }
  
  PluginConfigTable()
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
      return editors.get(r);
  }
  
  @Override public TableCellRenderer getCellRenderer(int r, int c)
  {
    if (c == 0)
      return this.getDefaultRenderer(String.class);
    else
      return renderers.get(r);
  }
  
  void prepare(Plugin plugin)
  {
    renderers.clear();
    editors.clear();
    
    model.arguments = plugin.getArguments();
    
    model.arguments.stream().map( a -> {
      if (a.getType().equals(Integer.class))
        return this.getDefaultEditor(Integer.class);
      else
        return this.getDefaultEditor(Object.class);
    }).forEach(editors::add);
    
    model.arguments.stream().map( a -> {
      if (a.getType().equals(Integer.class))
        return this.getDefaultRenderer(Integer.class);
      else
        return this.getDefaultRenderer(Object.class);
    }).forEach(renderers::add);
  }
}