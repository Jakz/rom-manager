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
import javax.swing.table.TableModel;

import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.table.SimpleListSelectionListener;
import com.pixbits.lib.ui.table.renderers.AlternateColorTableCellRenderer;

import jack.rm.GlobalSettings;
import jack.rm.Main;
import jack.rm.data.romset.GameSetManager;
import jack.rm.files.parser.DatUpdater;
import net.miginfocom.swing.MigLayout;

public class RomSetManagerView extends JPanel
{
  private final GameSetManager manager;
  
  private final JList<Platform> systemList;
  private final DefaultListModel<Platform> systemModel;
    
  private final SystemRomSetInfo systemSetInfo;
  
  @SuppressWarnings("rawtypes")
  private class SystemListCellRenderer implements ListCellRenderer<Platform>
  {
    ListCellRenderer renderer;

    SystemListCellRenderer(ListCellRenderer renderer)
    {
      this.renderer = renderer;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Component getListCellRendererComponent(JList<? extends Platform> list, Platform value, int index, boolean isSelected, boolean cellHasFocus) {
      JLabel label = (JLabel)renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      int count = manager.bySystem(value).size();
      
      label.setIcon(value.getIcon());
      label.setText(value.getName()+" ("+count+")");
      label.setForeground(count == 0 ? Color.GRAY : Color.BLACK);
    
      return label;
    }
  }
  
  private class DatTableModel extends AbstractTableModel
  {
    private final List<GameSet> data;
    
    private final String[] names = new String[] { "Enable", "Provider", "Flavour", "Identifier" };
    private final Class<?>[] classes = new Class<?>[] { Boolean.class, String.class, String.class, String.class };
    
    DatTableModel()
    {
      data = new ArrayList<GameSet>();
    }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return classes.length; }
    @Override public String getColumnName(int c) { return names[c]; }
    @Override public Class<?> getColumnClass(int c) { return classes[c]; }

    @Override
    public Object getValueAt(int r, int c)
    {
      GameSet rs = data.get(r);
      
      switch (c)
      {
        case 0: return GlobalSettings.settings.getEnabledProviders().contains(rs.ident()); 
        case 1: return rs.info().getName();
        case 2: return rs.info().getFlavour();
        case 3: return rs.ident();
        default: return null;
      }
    }
    
    @Override public boolean isCellEditable(int r, int c) { return c == 0 && data.get(r).isPresentOnDisk(); }
    
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
    
    public void setData(List<GameSet> data)
    {
      this.data.clear();
      this.data.addAll(data);
      this.fireTableDataChanged();
    }
  };
  
  class DatTable extends JTable
  {
    DatTable(TableModel model) { super(model); }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
      Component comp = super.prepareRenderer(renderer, row, column);
     
      if (column == 0)
        comp.setEnabled(((DatTableModel)getModel()).data.get(row).isPresentOnDisk());

      return comp;
    }
  }
  
  RomSetManagerView(GameSetManager manager)
  {
    this.manager = manager;
    
    systemSetInfo = new SystemRomSetInfo();

    systemModel = new DefaultListModel<>();
    Arrays.stream(Platforms.values()).sorted((o1, o2) -> o1.name.compareTo(o2.name)).forEach(s -> systemModel.addElement(s));
    
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
    
    private final DatTable datTable;
    private final DatTableModel datModel;
    
    private final SingleProviderInfo info;
    
    SystemRomSetInfo()
    {
      datModel = new DatTableModel();
      datTable = new DatTable(datModel);
      datTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      UIUtils.resizeTableColumn(datTable.getColumnModel().getColumn(0), 30);

      
      info = new SingleProviderInfo(datModel);

      datTable.getSelectionModel().addListSelectionListener(SimpleListSelectionListener.of(index -> info.updateFields(datModel.data.get(index))));

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
    
    public void updateFields(Platform platform)
    {      
      List<GameSet> sets = manager.bySystem(platform);
      int count = sets.size();

      countLabel.setIcon(platform.getIcon());
      
      if (count > 0)
        countLabel.setText(count+" available DATs for "+platform.getName());
      else
        countLabel.setText("No available DATs for "+platform.getName());
            
      datTable.clearSelection();
      datModel.setData(sets);
      info.updateFields(null);
    }
  }
  
  class SingleProviderInfo extends JPanel
  {
    private final DatTableModel model;
    
    private final JLabel name;
    private final JLabel status;
    private final JLabel type;
    private final JButton update;
        
    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    private GameSet set;
    
    SingleProviderInfo(DatTableModel model)
    {
      this.model = model;
      
      name = new JLabel(" ");
      status = new JLabel(" ");
      type = new JLabel(" ");
      update = new JButton(" ");
      
      name.setFont(name.getFont().deriveFont(Font.BOLD));
      
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
            Consumer<Boolean> postStep = r -> { 
              if (r) 
              {
                updateFields(set);
                model.fireTableDataChanged();

                try
                {
                  if (Main.current == set) //TODO: ugly design
                    Main.loadRomSet(set);
                } 
                catch (IOException e1)
                {
                  e1.printStackTrace();
                }
              }
            };
            
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
    
    public void updateFields(GameSet set)
    {
      this.set = set;
      
      if (set != null)
      {
        name.setText(set.info().getProvider().prettyName());
        
        try
        {
          boolean isPresent = set.isPresentOnDisk();
          
          update.setVisible(true);
          update.setEnabled(set.info().getProvider().canBeUpdated());
          
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

        type.setText("("+set.info().getProvider().getType().caption+")");
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
