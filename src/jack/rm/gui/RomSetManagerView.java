package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.data.console.System;
import jack.rm.data.romset.RomSet;
import jack.rm.data.romset.RomSetManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import com.pixbits.gui.AlternateColorTableCellRenderer;

public class RomSetManagerView extends JFrame
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
    
    private final String[] names = new String[] { "Provider", "Flavour", "Identifier", "Enable" };
    private final Class<?>[] classes = new Class<?>[] { String.class, String.class, String.class, Boolean.class };
    
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
        case 0: return rs.provider.getName();
        case 1: return rs.provider.getFlavour();
        case 2: return rs.ident();
        case 3: return GlobalSettings.settings.getEnabledProviders().contains(rs.ident());
        default: return null;
      }
    }
    
    @Override public boolean isCellEditable(int r, int c) { return c == 3; }
    
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
       
    getContentPane().setLayout(new BorderLayout());
    
    getContentPane().add(systemScrollPane, BorderLayout.WEST);
    getContentPane().add(systemSetInfo, BorderLayout.CENTER);
    
    setPreferredSize(new Dimension(600,400));
    setTitle("Rom Sets Management");
    pack();
  }
  
  void showMe()
  {
    setLocationRelativeTo(Main.mainFrame);
    setVisible(true);
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
        if (!e.getValueIsAdjusting())
          info.updateFields(datModel.data.get(e.getFirstIndex()));
      });
      
      JScrollPane datScrollPane = new JScrollPane(datTable);
      
      TableCellRenderer renderer = datTable.getDefaultRenderer(Boolean.class);
      datTable.setDefaultRenderer(Boolean.class, new AlternateColorTableCellRenderer(renderer));
      
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
    private final MigLayout layout;
    
    private RomSet set;
    
    SingleProviderInfo()
    {
      name = new JLabel();
      status = new JLabel();
      
      layout = new MigLayout();
      this.setLayout(new MigLayout("wrap 2, fill"));
      this.add(name, "spanx 2");
      this.add(status, "spanx 2");
    }
    
    public void updateFields(RomSet set)
    {
      this.set = set;
      
      if (set != null)
      {
        name.setText(set.provider.prettyName());
        
        try
        {
          if (Files.exists(set.datPath()))
            status.setText("Downloaded: YES ("+Files.getLastModifiedTime(set.datPath())+")");
          else
            status.setText("Downloaded: NO");
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      else
      {
        name.setText("");
        status.setText("");
      }
      
    }
  };
}
