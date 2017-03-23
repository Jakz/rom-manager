package jack.rm.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.handles.ArchiveHandle;
import com.pixbits.lib.io.archive.handles.BinaryHandle;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.NestedArchiveHandle;
import com.pixbits.lib.ui.color.ColorGenerator;
import com.pixbits.lib.ui.color.PastelColorGenerator;

import jack.rm.Main;
import jack.rm.files.ScanResult;

public class ClonesDialog extends JDialog
{
  private GameSet set;
  
  private final ColorGenerator colorGenerator;
  private final JTable table;
  private final CloneTableModel model;
  
  private List<ScanResult> clones = new ArrayList<>(); 
  private Map<ScanResult, Boolean> keep = new HashMap<>();
  private Map<Rom, Color> colors = new HashMap<>();
  
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
        case 2: return clones.get(r).path.relativePath();  
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
      updateStatus();
    }
    
    public void fireChanges() { this.fireTableDataChanged(); }
    
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
  
  private enum ClonePolicy
  {
    AUTO_SELECT_ALL("Auto-select all"),
    AUTO_SELECT_MISSING("Auto-select missing"),
    ;
    
    ClonePolicy(String caption) { this.caption = caption; }
    public String toString() { return caption; }
    
    public final String caption;
  }
  
  private enum ClonePriority
  {
    ANY("Any", null),
    NESTED("Nested Aarchive", NestedArchiveHandle.class),
    ARCHIVED("Archive", ArchiveHandle.class),
    BINARY("Binary", BinaryHandle.class)
    ;
    
    ClonePriority(String caption, Class<? extends Handle> type) { this.caption = caption; this.type = type; }
    public String toString() { return caption; }
    
    public final String caption;
    public final Class<? extends Handle> type;
  }
  
  private final JComboBox<ClonePolicy> clonePolicy = new JComboBox<>(ClonePolicy.values());
  private final JComboBox<ClonePriority> clonePriority = new JComboBox<>(ClonePriority.values());
  private final JLabel status = new JLabel();
  private final JButton autoSelect = new JButton("Auto-select");
  private final JButton apply = new JButton("Apply");
  private final JButton reset = new JButton("Reset");
  
  public ClonesDialog(Frame frame, String title)
  {
    super(frame, title);
    
    colorGenerator = new PastelColorGenerator();
    
    model = new CloneTableModel();
    table = new JTable(model);
    JScrollPane pane = new JScrollPane(table);
    pane.setPreferredSize(new java.awt.Dimension(800,600));
    
    table.setDefaultRenderer(Boolean.class, model.new Renderer(model.new BooleanTableCellRenderer()));
    table.setDefaultRenderer(String.class, model.new Renderer(new DefaultTableCellRenderer()));
    
    autoSelect.addActionListener( e -> autoChoose(clonePolicy.getItemAt(clonePolicy.getSelectedIndex()), clonePriority.getItemAt(clonePriority.getSelectedIndex())));
    reset.addActionListener( e -> {
      this.keep = this.clones.stream().collect(Collectors.toMap( c -> c, c -> false));
      model.fireChanges();
      updateStatus();
    });
    apply.addActionListener( e -> {
      int total = colors.size();
      int selected = keep.values().stream().mapToInt( ee -> ee ? 1 : 0).sum();
      
      // if all clones have been assigned
      if (total == selected)
      {
        apply();
        setVisible(false);
      }
      else
      {
        Dialogs.showQuestion("Missing rom", (total-selected)+" roms have no specific clone to keep set,\ndo you want to auto-complete them?", this, 
          () -> {
            autoChoose(clonePolicy.getItemAt(clonePolicy.getSelectedIndex()), clonePriority.getItemAt(clonePriority.getSelectedIndex()));
            apply();
            setVisible(false);
          }
        );
        
      }
      
    });
    
    //pane.setPreferredSize(new Dimension());
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(pane, BorderLayout.CENTER);
    
    JPanel options = new JPanel();
    options.setLayout(new BoxLayout(options, BoxLayout.LINE_AXIS));
    options.add(new JLabel("Policy: "));
    options.add(clonePolicy);
    options.add(new JLabel("Priority: "));
    options.add(clonePriority);
    options.add(autoSelect);
    options.add(reset);
    options.add(apply);
    status.setBorder(BorderFactory.createLoweredBevelBorder());
    options.add(status);
    
    panel.add(options,BorderLayout.SOUTH);
    
    this.add(panel);
    
    pack();
  }
  
  public void updateStatus()
  {
    int total = colors.size();
    int selected = keep.values().stream().mapToInt( e -> e ? 1 : 0).sum();
    status.setText("Selected "+selected+" of "+total);
  }
  
  public void apply()
  {
    keep.forEach( (k, v) -> { if (v) k.assign(); });
    set.checkNames();
    set.refreshStatus();
    Main.mainFrame.updateTable();
  }
  
  public void activate(GameSet set, Set<ScanResult> clones)
  {
    this.set = set;
    this.keep.clear();
    this.clones.clear();
    
    // TODO: probably requires to be rewritten almost totally to manage multiple roms per game
    Set<Rom> romClones = clones.stream().map( c -> c.rom ).collect(Collectors.toSet());
   
    this.clones = new ArrayList<>(clones);
    this.clones.addAll(set.stream()
        .flatMap(g -> g.stream())
        .filter( r -> romClones.contains(r))
        .map( r -> new ScanResult(r, r.handle()) )
        .collect(Collectors.toList()));
    
    Collections.sort(this.clones);
    this.keep = this.clones.stream().collect(Collectors.toMap( c -> c, c -> false));
    this.colors = romClones.stream().collect(Collectors.toMap( c -> c, c -> colorGenerator.getColor()));
    
    updateStatus();
    
    model.fireChanges();
    this.setLocationRelativeTo(null);
    this.setVisible(true);
    Dialogs.showWarning("Clones Found", colors.size()+" clones have been found,\nplease specify which entries you want to keep", this);
  }
  
  public void autoChoose(ClonePolicy policy, ClonePriority priority)
  {
    if (policy == ClonePolicy.AUTO_SELECT_ALL)
      this.keep = this.clones.stream().collect(Collectors.toMap( c -> c, c -> false));
    
    List<List<ScanResult>> results = new ArrayList<>();
    LinkedList<ScanResult> current = null;
    
    /* create a list for each rom with clones */
    boolean alreadySet = false;
    for (ScanResult result : this.clones)
    {
      if (current == null || !current.peekLast().rom.equals(result.rom))
      {
        if (current != null && !alreadySet)
          results.add(current);
        
        alreadySet = false;
        current = new LinkedList<>();
      }

      current.add(result);
      alreadySet |= keep.get(result);
    } 
    
    if (current != null && !alreadySet)
      results.add(current);
    
    for (List<ScanResult> list : results)
    {
      ScanResult result = chooseBestClone(list, policy, priority);
      keep.put(result, true);
    }
    
    model.fireChanges();
    updateStatus();
  }
  
  public ScanResult chooseBestClone(List<ScanResult> results, ClonePolicy policy, ClonePriority priority)
  {
    if (priority != ClonePriority.ANY)
    {
      List<ScanResult> filtered = results.stream().filter(r -> r.path.getClass() == priority.type).collect(Collectors.toList());
      if (!filtered.isEmpty())
        results = filtered;
    }
    
    boolean hasRenamer = set.getSettings().getRenamer() != null;
    boolean hasMover = set.getSettings().getFolderOrganizer() != null;
    
    Predicate<ScanResult> predicateAny = e -> true;
    Predicate<ScanResult> predicateCorrectFolder = e -> !hasMover || set.getSettings().romsPath.resolve(e.rom.game().getCorrectFolder()).equals(e.path.path().getParent());
    Predicate<ScanResult> predicateCorrectName = e -> !hasRenamer || e.rom.game().getCorrectName().equals(e.path.path().getFileName());
    Predicate<ScanResult> predicateCorrectNameAndFolder = predicateCorrectName.and(predicateCorrectFolder);
    
    List<Predicate<ScanResult>> predicates = Arrays.asList(
      predicateCorrectNameAndFolder,
      predicateCorrectName,
      predicateCorrectFolder,
      predicateAny
    );
    
    Optional<ScanResult> optional = Optional.empty();
     for (Predicate<ScanResult> predicate : predicates)
       if ((optional = results.stream().filter(predicate).findFirst()).isPresent())
         break;

    ScanResult chosen = optional.get();
    
    return chosen;
  }
}
