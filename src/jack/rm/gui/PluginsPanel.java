package jack.rm.gui;

import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import com.pixbits.plugin.PluginBuilder;
import com.pixbits.plugin.PluginManager;
import com.pixbits.plugin.gui.PluginConfigTable;

import net.miginfocom.swing.MigLayout;

import jack.rm.Settings;
import jack.rm.data.set.RomSet;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;

import java.awt.event.ItemEvent;

public class PluginsPanel extends JPanel
{
  private final JTable table;
  private final PluginTableModel model;
  private final PluginManager<ActualPlugin, ActualPluginBuilder> manager;
  
  class PluginTableModel extends AbstractTableModel
  {
    final List<ActualPluginBuilder> plugins = new ArrayList<>();
    
    private final String[] columnNames = { "Name", "Category", "Enabled" };
    private final Class<?>[] columnClasses = { String.class, String.class, Boolean.class };
    
    @Override public int getColumnCount() { return columnClasses.length; }
    @Override public String getColumnName(int i) { return columnNames[i]; }
    @Override public Class<?> getColumnClass(int i) { return columnClasses[i]; }
    @Override public int getRowCount() { return plugins.size(); }
    @Override public boolean isCellEditable(int r, int c) { return c == 2 && plugins.get(r).isCompatible(RomSet.current); }
    
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
    
    public void populate()
    {
      clear();
            
      manager.stream()
        .filter(filter.getItemAt(filter.getSelectedIndex())::test)
        .forEach(plugins::add);
      
      fireChanges();
    }
    
    public void fireChanges()
    {
      this.fireTableDataChanged();
    }
  }
  
  PluginConfigTable configTable;
  
  MigLayout layout;
  
  private enum PluginFilter
  {
    ALL
    { 
      public String toString() { return "Show All"; } 
      public boolean test(ActualPluginBuilder builder) { return true; }
    },
    COMPATIBLE
    {
      public String toString() { return "Show Compatible"; }
      public boolean test(ActualPluginBuilder builder) { return builder.isCompatible(RomSet.current); }
    },
    ENABLED
    {
      public String toString() { return "Show Enabled"; }
      public boolean test(ActualPluginBuilder builder) { 
        Optional<ActualPlugin> plugin = Settings.current().plugins.getPlugin(builder.getID());
        return plugin.isPresent() && plugin.get().isEnabled();
      }
    };
    
    public abstract boolean test(ActualPluginBuilder builder);
    
  }
  
  private final JLabel[] labels;
  private final JTextArea desc;
  private final JComboBox<PluginFilter> filter;
  
  public void updateFields(PluginBuilder<ActualPlugin> builder)
  {
    labels[0].setText("Name: "+builder.info.getSimpleName());
    labels[1].setText("Category: "+builder.type);
    labels[2].setText("Author: "+builder.info.author);
    desc.setText(builder.info.description);
  }
  
  public PluginsPanel(PluginManager<ActualPlugin, ActualPluginBuilder> manager)
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
    
    filter = new JComboBox<>(PluginFilter.values());
    filter.addItemListener( e -> { if (e.getStateChange() == ItemEvent.SELECTED) populate(); });
    
    
    configTable = new PluginConfigTable();
    JScrollPane configPane = new JScrollPane(configTable);
    
    JScrollPane tablePane = new JScrollPane(table);

    JScrollPane descPane = new JScrollPane(desc);
    descPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    descPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);    
    
    layout = new MigLayout();
    this.setLayout(new MigLayout("wrap 8, fill"));
    this.add(tablePane, "span 6 10, grow");
    this.add(labels[0], "spanx 2");
    this.add(labels[1], "spanx 2");
    this.add(labels[2], "spanx 2");
    this.add(new JLabel("Description:"), "spanx 2");
    this.add(descPane, "span 2 2, width 10:300:, height 80:100:150, growprio 50, grow");
    this.add(configPane, "span 2 2, height 100:150:200, grow");
    this.add(filter, "cell 3 10, spanx 2");

  }
  
  public void populate()
  {
    model.populate();
  }
}
