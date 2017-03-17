package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.pixbits.lib.ui.table.renderers.AlternateColorTableCellRenderer;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.data.console.System;
import jack.rm.data.romset.RomSet;
import jack.rm.data.romset.RomSetManager;
import jack.rm.files.parser.DatUpdater;
import net.miginfocom.swing.MigLayout;

public class RomSetManagerView extends JPanel
{
  private final JList<System> systemList;
  private final DefaultListModel<System> systemModel;
    
  private final SystemRomSetInfo systemSetInfo;
  
  @SuppressWarnings("rawtypes")
  private class SystemListCellRenderer implements ListCellRenderer<System>
  {
    ListCellRenderer renderer;

    SystemListCellRenderer(ListCellRenderer renderer)
    {
      this.renderer = renderer;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<? extends System> list, System value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel)renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      int count = RomSetManager.bySystem(value).size();
      
      label.setIcon(value.getIcon());
      label.setText(value.name+" ("+count+")");
      label.setForeground(count == 0 ? Color.GRAY : Color.BLACK);
    
      return label;
    }
  }
  
  private class DatTableModel extends AbstractTableModel
  {
    private final List<RomSet> data;
    
    private final String[] names = new String[] { "Enable", "Provider", "Flavour", "Identifier" };
    private final Class<?>[] classes = new Class<?>[] { Boolean.class, String.class, String.class, String.class };
    
    DatTableModel()
    {
      data = new ArrayList<RomSet>();
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return classes.length; }
    @Override public String getColumnName(int c) { return names[c]; }
    @Override public Class<?> getColumnClass(int c) { return classes[c]; }

    @Override
    public Object getValueAt(int r, int c)
    {
      RomSet rs = data.get(r);
      
      switch (c)
      {
        case 0: return GlobalSettings.settings.getEnabledProviders().contains(rs.ident()); 
        case 1: return rs.provider.getName();
        case 2: return rs.provider.getFlavour();
        case 3: return rs.ident();
        default: return null;
      }
    }
    
    @Override public boolean isCellEditable(int r, int c) { return c == 0; }
    
    @Override public void setValueAt(Object value, int r, int c)
    {
      boolean f = (boolean)value;
      
      if (f)
        GlobalSettings.settings.enableProvider(data.get(r).ident());
      else
        GlobalSettings.settings.disableProvider(data.get(r).ident());
      
      Main.mainFrame.rebuildEnabledDats();
      this.fireTableDataChanged();
    }
    
    public void setData(List<RomSet> data)
    {
      this.data.clear();
      this.data.addAll(data);
      this.fireTableDataChanged();
    }
  };
  
  RomSetManagerView()
  {
    systemSetInfo = new SystemRomSetInfo();

    systemModel = new DefaultListModel<>();
    Arrays.stream(System.values()).sorted((o1, o2) -> o1.name.compareTo(o2.name)).forEach(s -> systemModel.addElement(s));
    
    systemList = new JList<>();
    systemList.setModel(systemModel);
    systemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    systemList.setCellRenderer(new SystemListCellRenderer(new DefaultListCellRenderer()));
    systemList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting())
        systemSetInfo.updateFields(systemList.getSelectedValue());
    });
    
    
    JScrollPane systemScrollPane = new JScrollPane(systemList);
    systemScrollPane.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
       
    setLayout(new BorderLayout());
    
    add(systemScrollPane, BorderLayout.WEST);
    add(systemSetInfo, BorderLayout.CENTER);
  }
    
  class SystemRomSetInfo extends JPanel
  {
    private final JLabel countLabel;
    
    private final JTable datTable;
    private final DatTableModel datModel;
    
    private final SingleProviderInfo info;
    
    SystemRomSetInfo()
    {
      info = new SingleProviderInfo();

      datModel = new DatTableModel();
      datTable = new JTable(datModel);
      datTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
      datTable.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting() && e.getFirstIndex() < datModel.data.size())
          info.updateFields(datModel.data.get(e.getFirstIndex()));
      });
      
      JScrollPane datScrollPane = new JScrollPane(datTable);
      
      TableCellRenderer renderer = datTable.getDefaultRenderer(Boolean.class);
      datTable.setDefaultRenderer(Boolean.class, new AlternateColorTableCellRenderer(renderer));
      
      /*{
        TableCellRenderer stringRenderer = datTable.getDefaultRenderer(String.class);
      
        Predicate<RomSet> predicate = r -> r.canBeLoaded();
        Consumer<JComponent> trueEffect = c -> c.setForeground(Color.GREEN);
        Consumer<JComponent> falseEffect = c -> c.setForeground(UIManager.getColor("Table.foreground"));
        
        datTable.setDefaultRenderer(String.class, new LambdaTableCellRenderer<RomSet>(predicate, trueEffect, falseEffect, stringRenderer));
      }*/
      
      
      JTableHeader header = datTable.getTableHeader();
      header.setFont(header.getFont().deriveFont(header.getFont().getSize2D()-4));
      
      countLabel = new JLabel();
      countLabel.setHorizontalAlignment(SwingConstants.LEFT);
           
      setLayout(new BorderLayout());
      add(countLabel, BorderLayout.NORTH);
      add(datScrollPane, BorderLayout.CENTER);
      add(info, BorderLayout.SOUTH);
    }
    
    public void updateFields(System system)
    {      
      List<RomSet> sets = RomSetManager.bySystem(system);
      int count = sets.size();

      countLabel.setIcon(system.getIcon());
      
      if (count > 0)
        countLabel.setText(count+" available DATs for "+system.name);
      else
        countLabel.setText("No available DATs for "+system.name);
            
      datTable.clearSelection();
      datModel.setData(sets);
      info.updateFields(null);
    }
  }
  
  class SingleProviderInfo extends JPanel
  {
    private final JLabel name;
    private final JLabel status;
    private final JLabel type;
    private final JButton update;
    
    
    private final MigLayout layout;
    
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    private RomSet set;
    
    SingleProviderInfo()
    {
      name = new JLabel(" ");
      status = new JLabel(" ");
      type = new JLabel(" ");
      update = new JButton(" ");
      
      name.setFont(name.getFont().deriveFont(Font.BOLD));
      
      layout = new MigLayout();
      this.setLayout(new MigLayout("wrap 8, fill"));
      this.add(name, "spanx 6");
      this.add(type, "spanx 2, wrap");
      this.add(status);
      this.add(update, "gapleft 20, wrap");
      
      update.setVisible(false);
      update.addActionListener(e -> {
        if (set != null)
        {
          try
          {
            Consumer<Boolean> postStep = r -> { if (r) updateFields(set); };
            
            DatUpdater.updateDat(set, postStep);
          }
          catch (IOException ex)
          {
            ex.printStackTrace();
          }
        }
      });
      
      this.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
    }
    
    public void updateFields(RomSet set)
    {
      this.set = set;
      
      if (set != null)
      {
        name.setText(set.provider.prettyName());
        
        try
        {
          boolean isPresent = Files.exists(set.datPath());
          
          update.setVisible(true);
          update.setEnabled(set.provider.canBeUpdated());
          
          if (isPresent)
          {
            status.setForeground(new Color(0,180,0));
            status.setText("Status: PRESENT ("+format.format(new Date(Files.getLastModifiedTime(set.datPath()).toMillis()))+")");
            update.setText("Update");
          }
          else
          {
            status.setForeground(new Color(180,0,0));
            status.setText("Status: MISSING");
            update.setText("Download");
          }
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

        type.setText("("+set.provider.getType().caption+")");
      }
      else
      {
        name.setText(" ");
        status.setText(" ");
        type.setText(" ");
        update.setVisible(false);
      }
      
    }
  };
}