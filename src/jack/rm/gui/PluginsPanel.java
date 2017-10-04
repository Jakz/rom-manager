package jack.rm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.plugin.PluginBuilder;
import com.pixbits.lib.plugin.PluginManager;
import com.pixbits.lib.plugin.ui.PluginConfigTable;

import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.ActualPluginBuilder;
import jack.rm.plugins.PluginRealType;
import net.miginfocom.swing.MigLayout;

public class PluginsPanel extends JPanel
{
  private final JTable table;
  private final PluginTableModel model;
  private final PluginManager<ActualPlugin, ActualPluginBuilder> manager;
  //TODO: should not be static
  private static GameSetManager setManager;
  private GameSet romset;
  
  private class PluginCellRenderer implements TableCellRenderer
  {
    private final TableCellRenderer inner;
    
    PluginCellRenderer(TableCellRenderer inner)
    {
      this.inner = inner;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      JComponent component = (JComponent)inner.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      ActualPluginBuilder builder = model.plugins.get(row);
      
      boolean compatible = builder.isCompatible(romset);
      
      component.setOpaque(true);
      component.setEnabled(compatible);
      component.setForeground(compatible ? Color.BLACK : Color.GRAY);
      
      return component;
    }
  }
  
  private class PluginTableModel extends AbstractTableModel
  {
    final List<ActualPluginBuilder> plugins = new ArrayList<>();
    
    private final String[] columnNames = { "Name", "Category", "Enabled" };
    private final Class<?>[] columnClasses = { String.class, String.class, Boolean.class };
    
    @Override public int getColumnCount() { return columnClasses.length; }
    @Override public String getColumnName(int i) { return columnNames[i]; }
    @Override public Class<?> getColumnClass(int i) { return columnClasses[i]; }
    @Override public int getRowCount() { return plugins.size(); }
    @Override public boolean isCellEditable(int r, int c) { return c == 2 && plugins.get(r).isCompatible(romset); }
    
    @Override public Object getValueAt(int r, int c)
    {
      PluginBuilder<ActualPlugin> builder = plugins.get(r);
      
      switch (c)
      {
        case 0: return builder.info.getSimpleName();
        case 1: return builder.type;
        case 2:
        {
          Optional<ActualPlugin> plugin = setManager.settings(romset).plugins.getPlugin(builder.getID());
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
        setManager.settings(romset).plugins.enable(manager, plugins.get(r).getID());
      else if (!plugins.get(r).type.isRequired())
        setManager.settings(romset).plugins.disable(plugins.get(r).getID());
      
      MyGameSetFeatures helper = Main.current.helper();
      helper.pluginStateChanged();
      Main.mainFrame.pluginStateChanged();

      //TODO: enabling or disabling a plugin should have an effect in multiple parts of the UI
      
      fireChanges();
    }
    
    public void clear() { plugins.clear(); }
    
    public void populate()
    {
      clear();
            
      manager.stream()
        .filter(b -> filter.getItemAt(filter.getSelectedIndex()).test(b, romset))
        .sorted( (p1, p2) -> ((PluginRealType)p1.type).compareTo((PluginRealType)p2.type))
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
    COMPATIBLE
    {
      public String toString() { return "Show Compatible"; }
      public boolean test(ActualPluginBuilder builder, GameSet romset) { return builder.isCompatible(romset); }
    },
    ENABLED
    {
      public String toString() { return "Show Enabled"; }
      public boolean test(ActualPluginBuilder builder, GameSet romset) { 
        Optional<ActualPlugin> plugin = setManager.settings(romset).plugins.getPlugin(builder.getID());
        return plugin.isPresent() && plugin.get().isEnabled();
      }
    },
    ALL
    { 
      public String toString() { return "Show All"; } 
      public boolean test(ActualPluginBuilder builder, GameSet romset) { return true; }
    }
    ;
    
    public abstract boolean test(ActualPluginBuilder builder, GameSet romset);
    
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
  
  public PluginsPanel(PluginManager<ActualPlugin, ActualPluginBuilder> manager, GameSetManager setManager)
  {
    this.manager = manager;
    this.model = new PluginTableModel();
    this.table = new JTable(model);
    this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    TableColumnModel columnModel = table.getColumnModel();
    
    columnModel.getColumn(2).setMinWidth(40);
    columnModel.getColumn(2).setMaxWidth(40);
    
    columnModel.getColumn(0).setCellRenderer(new PluginCellRenderer(new DefaultTableCellRenderer()));
    columnModel.getColumn(1).setCellRenderer(new PluginCellRenderer(new DefaultTableCellRenderer()));
    columnModel.getColumn(2).setCellRenderer(new PluginCellRenderer(table.getDefaultRenderer(Boolean.class)));

 
    //((JComponent)this.table.getDefaultRenderer(Boolean.class)).setOpaque(true);
    
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
          
          Optional<ActualPlugin> plugin = setManager.settings(romset).plugins.getPlugin(model.plugins.get(r).getID());
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
    filter.addItemListener( e -> { if (e.getStateChange() == ItemEvent.SELECTED) populate(romset); });
    
    
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
  
  public void populate(GameSet romset)
  {
    this.romset = romset;
    model.populate();
  }
}
