package jack.rm.gui.plugins;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import jack.rm.Settings;
import jack.rm.plugin.*;
import jack.rm.plugins.ActualPlugin;

import java.awt.*;

public class PluginsPanel extends JPanel
{
  private final JTable table;
  private final PluginTableModel model;
  private final PluginManager<ActualPlugin> manager;
  
  class PluginTableModel extends AbstractTableModel
  {
    final List<PluginBuilder<ActualPlugin>> plugins = new ArrayList<>();
    
    private final String[] columnNames = { "Name", "Category", "Enabled" };
    private final Class<?>[] columnClasses = { String.class, String.class, Boolean.class };
    
    @Override public int getColumnCount() { return columnClasses.length; }
    @Override public String getColumnName(int i) { return columnNames[i]; }
    @Override public Class<?> getColumnClass(int i) { return columnClasses[i]; }
    @Override public int getRowCount() { return plugins.size(); }
    @Override public boolean isCellEditable(int r, int c) { return c == 2; }
    
    @Override public Object getValueAt(int r, int c)
    {
      PluginBuilder<ActualPlugin> builder = plugins.get(r);
      
      switch (c)
      {
        case 0: return builder.info.getSimpleName();
        case 1: return builder.type;
        case 2:
        {
          Optional<ActualPlugin> plugin = Settings.current().plugins.getPlugin(builder.getID());
          return plugin.isPresent() && plugin.get().isEnabled();
        }
        default: return null;
      }
    }
    
    @Override public void setValueAt(Object o, int r, int c)
    {
      updateFields(plugins.get(r));
      
      Boolean b = (Boolean)o;
      
      if (b)
        Settings.current().plugins.enable(manager, plugins.get(r).getID());
      else
        Settings.current().plugins.disable(plugins.get(r).getID());
      
      fireChanges();
    }
    
    public void clear() { plugins.clear(); }
    
    public void populate(boolean showOnlyEnabled)
    {
      clear();
      
      manager.stream()
        .filter( b -> !showOnlyEnabled || Settings.current().plugins.hasPlugin(b.getID()))
        .forEach(plugins::add);
    }
    
    public void fireChanges()
    {
      this.fireTableDataChanged();
    }
  }
  
  PluginConfigTable configTable;
  
  MigLayout layout;
  
  private final JLabel[] labels;
  private final JTextArea desc;
  
  public void updateFields(PluginBuilder<ActualPlugin> builder)
  {
    labels[0].setText("Name: "+builder.info.getSimpleName());
    labels[1].setText("Category: "+builder.type);
    labels[2].setText("Author: "+builder.info.author);
    desc.setText(builder.info.description);
  }
  
  public PluginsPanel(PluginManager<ActualPlugin> manager)
  {
    this.manager = manager;
    this.model = new PluginTableModel();
    this.table = new JTable(model);
    this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    this.table.getColumnModel().getColumn(2).setMinWidth(40);
    this.table.getColumnModel().getColumn(2).setMaxWidth(40);
    
    ((JComponent)this.table.getDefaultRenderer(Boolean.class)).setOpaque(true);
    
    JTableHeader header = table.getTableHeader();
    header.setFont(header.getFont().deriveFont(header.getFont().getSize2D()-4));

    this.table.getSelectionModel().addListSelectionListener( e -> {
      if (!e.getValueIsAdjusting())
      {
        int r = table.getSelectedRow();
        if (r != -1)
        {
          r = table.convertRowIndexToModel(r);
          updateFields(model.plugins.get(r));
          
          if (configTable.isEditing())
            configTable.getCellEditor().stopCellEditing();
          
          Optional<ActualPlugin> plugin = Settings.current().plugins.getPlugin(model.plugins.get(r).getID());
          configTable.prepare(plugin.orElse(null));    
        }
      }
    });
    
    labels = new JLabel[3];
    labels[0] = new JLabel("Name:");
    labels[1] = new JLabel("Category:");
    labels[2] = new JLabel("Author:");
    
    desc = new JTextArea();
    desc.setLineWrap(true);
    desc.setWrapStyleWord(true);
    desc.setEditable(false);
    
    
    configTable = new PluginConfigTable();
    JScrollPane configPane = new JScrollPane(configTable);
    
    JScrollPane tablePane = new JScrollPane(table);

    JScrollPane descPane = new JScrollPane(desc);
    descPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    descPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);    
    
    layout = new MigLayout();
    this.setLayout(new MigLayout("wrap 8, fill"));
    this.add(tablePane, "span 6 11, grow");
    this.add(labels[0], "spanx 2");
    this.add(labels[1], "spanx 2");
    this.add(labels[2], "spanx 2");
    this.add(new JLabel("Description:"), "spanx 2");
    this.add(descPane, "span 2 2, width 10:300:, height 80:100:150, growprio 50, grow");
    this.add(configPane, "span 2 2, height 100:150:200, grow");
  }
  
  public void populate()
  {
    model.populate(false);
  }
}
