package jack.rm.gui;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.github.jakz.romlib.ui.Icon;

import jack.rm.GlobalSettings;
import jack.rm.data.romset.RomSet;
import jack.rm.data.romset.RomSetManager;
import net.miginfocom.swing.MigLayout;

public class RomSetOrderView extends JPanel
{
  private enum SortMode
  {
    NAME("by name"),
    PLATFORM("by platform"),
    PROVIDER("by provider")
    ;
    
    public final String caption;
    
    SortMode(String caption) { this.caption = caption; }
  };
  
  private final JList<RomSet> list;
  private final DefaultListModel<RomSet> model = new DefaultListModel<>(); 
  
  private final JButton moveUp;
  private final JButton moveDown;
  private final JComboBox<SortMode> sortMode;
  
  private final MigLayout layout;
  
  RomSetOrderView()
  {
    list = new JList<>();
    list.setCellRenderer(new RomSetListCellRenderer());
    list.setModel(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    moveUp = new JButton(Icon.ARROW_UP.getIcon());
    moveDown = new JButton(Icon.ARROW_DOWN.getIcon());
    
    sortMode = new JComboBox<>();
    
    layout = new MigLayout("wrap");

   
    add(new JScrollPane(list));
    add(moveUp);
    add(moveDown);
  }
  
  void showMe()
  {
    model.clear();
    
    GlobalSettings.settings.getEnabledProviders().stream().map(RomSetManager::byIdent).forEach(model::addElement); 
  }
}
