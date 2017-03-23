package jack.rm.plugins.renamer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.AbstractTableModel;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.Settings;
import jack.rm.files.Organizer;
import jack.rm.files.Pattern;
import jack.rm.gui.PluginOptionsPanel;
import jack.rm.i18n.Text;

public class PatternRenamerPlugin extends RenamerPlugin
{
  @ExposedParameter(name="Open Block") private String openBlock = " [";
  @ExposedParameter(name="Close Block") private String closeBlock = "]";
    
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Pattern Renamer", new PluginVersion(1,0), "Jack",
        "This plugins enables support for renaming through pattern sets.");
  }
  
  @Override public String getCorrectName(Game rom)
  {
    Pattern.RenamingOptions options = new Pattern.RenamingOptions(openBlock, closeBlock);
    
    String temp = new String(GameSet.current.getSettings().renamingPattern);
    
    Set<Pattern> patterns = Organizer.getPatterns(GameSet.current);
    
    for (Pattern p : patterns)
      temp = p.apply(options, temp, rom);
    
    return temp;
  }
  
  @Override public String getCorrectInternalName(Game rom)
  {
    Pattern.RenamingOptions options = new Pattern.RenamingOptions(openBlock, closeBlock);

    
    String temp = new String(GameSet.current.getSettings().internalRenamingPattern != null ? GameSet.current.getSettings().internalRenamingPattern : GameSet.current.getSettings().renamingPattern);
    
    Set<Pattern> patterns = Organizer.getPatterns(GameSet.current);
    
    for (Pattern p : patterns)
      temp = p.apply(options, temp, rom);
    
    return temp;
  }
  
  @Override public PluginOptionsPanel getGUIPanel() { return new PatternRenamerPanel(); } 
  
  private enum ArchiveRenameMode
  {
    None("None"),
    Same("Same"),
    Custom("Custom")
    ;
    public final String caption;
    
    ArchiveRenameMode(String caption) { this.caption = caption; }
  }
  
  private class PatternRenamerPanel extends PluginOptionsPanel implements CaretListener
  {
    private static final long serialVersionUID = 1L;
    
    private JTextField patternField = new JTextField(30);
    private JTextField internalPatternField = new JTextField(20);
    
    private JComboBox<ArchiveRenameMode> internalRenameMode = new JComboBox<ArchiveRenameMode>(ArchiveRenameMode.values()); 
    
    private JTextField exampleField = new JTextField(30);
    

    private JTable patternsTable;
    private List<Pattern> patterns = new ArrayList<Pattern>();
    
    public class TableModel extends AbstractTableModel
    {
      private static final long serialVersionUID = 1L;
      
      @Override public int getColumnCount() { return 2; }
      @Override public int getRowCount() { return patterns.size();}
      @Override public String getColumnName(int col) { return col == 0 ? "Code" : "Description"; }
      
      @Override public Object getValueAt(int row, int col) {
        Pattern p = patterns.get(row);
        return col == 0 ? p.code : p.desc;
      }
    };
    
    private TableModel model = new TableModel();
    
    public PatternRenamerPanel()
    {
      JPanel fields = new JPanel(new GridBagLayout());
      JPanel main = new JPanel(new BorderLayout());
      main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
      
      internalRenameMode.addActionListener(e -> switchToInternalRenamerMode((ArchiveRenameMode)internalRenameMode.getSelectedItem()));
      
      GridBagConstraints c = new GridBagConstraints();
      
      updateGBC(c, 0,0,1,1);
      c.ipadx = 5;
      fields.add(new JLabel(Text.TEXT_RENAMER_PATTERN.text()+":"), c);
      
      updateGBC(c, GridBagConstraints.RELATIVE,0,4,1);
      c.ipadx = 0;
      fields.add(patternField,c);
            
      updateGBC(c, 0, GridBagConstraints.RELATIVE,1,1);
      c.ipadx = 5;
      fields.add(new JLabel(Text.TEXT_RENAMER_EXAMPLE.text()+":"),c);
      
      updateGBC(c, GridBagConstraints.RELATIVE,1,4,1);
      c.ipadx = 0;
      fields.add(exampleField,c);
      
      updateGBC(c, 0, GridBagConstraints.RELATIVE,1,1);
      c.ipadx = 5;
      fields.add(new JLabel(Text.TEXT_INTERNAL_PATTERN.text()+":"), c);
      
      updateGBC(c, GridBagConstraints.RELATIVE,2,1,1);
      c.ipadx = 0;
      fields.add(internalRenameMode, c);
      
      updateGBC(c, GridBagConstraints.RELATIVE,2,3,1);
      fields.add(internalPatternField, c);
      
      patternsTable = new JTable(model);
      patternsTable.setAutoCreateRowSorter(true);
      patternsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      patternsTable.addMouseListener(
          new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
              if (e.getClickCount() == 2){
                int r = patternsTable.getSelectedRow();
                
                if (r != -1)
                {
                  r = patternsTable.convertRowIndexToModel(r);
                  String code = patterns.get(r).code;
                  int p = patternField.getCaretPosition();
                  String before = patternField.getText().substring(0, p);
                  String after = patternField.getText().substring(p);
                  patternField.setText(before+code+after);
                  caretUpdate(null);
                  patternField.setCaretPosition(p+2);
                  patternField.requestFocusInWindow();
                }
              }
            }
          });
      patternsTable.getColumnModel().getColumn(0).setMinWidth(50);
      patternsTable.getColumnModel().getColumn(0).setMaxWidth(50);
          
      exampleField.setEnabled(false);
      exampleField.setDisabledTextColor(Color.BLACK);  
      patternField.addCaretListener(this);
      internalPatternField.addCaretListener(this);

      JScrollPane scrollPane = new JScrollPane(patternsTable);
      main.add(fields,BorderLayout.NORTH);
      main.add(scrollPane,BorderLayout.CENTER);
      this.add(main);
      
      //pack();
    }
    
    public void updateGBC(GridBagConstraints c, int x, int y, int w, int h)
    {
      c.gridx = x; c.gridy = y; c.gridwidth = w; c.gridheight = h;
    }

    public void updateFields()
    {
      Settings settings = getRomset().getSettings();
      
      patternField.setText(settings.renamingPattern);
      patterns.clear();
      Organizer.getPatterns(getRomset()).forEach(patterns::add);
      // TODO: should be invoked even when plugins are changed
      
      if (!settings.shouldRenameInternalName)
        switchToInternalRenamerMode(ArchiveRenameMode.None);
      else if (settings.internalRenamingPattern == null)
        switchToInternalRenamerMode(ArchiveRenameMode.Same);
      else
        switchToInternalRenamerMode(ArchiveRenameMode.Custom);
    }
    
    private void switchToInternalRenamerMode(ArchiveRenameMode mode)
    {
      Settings settings = getRomset().getSettings();

      
      internalRenameMode.setSelectedItem(mode);
      
      if (mode == ArchiveRenameMode.None)
      {
        internalPatternField.setText("");
        internalPatternField.setEnabled(false);
        settings.shouldRenameInternalName = false;
      }
      else if (mode == ArchiveRenameMode.Custom)
      {
        internalPatternField.setEnabled(true);
        internalPatternField.setText(settings.internalRenamingPattern);
        settings.shouldRenameInternalName = true;
      }
      else if (mode == ArchiveRenameMode.Same)
      {
        internalPatternField.setEnabled(false);
        internalPatternField.setText(settings.renamingPattern);
        settings.shouldRenameInternalName = true;
        settings.internalRenamingPattern = null;
      }
    }

    @Override
    public void caretUpdate(CaretEvent e)
    {
      if (e.getSource() == patternField)
      {
        getRomset().getSettings().renamingPattern = patternField.getText();
        exampleField.setText(GameSet.current.getAny().getCorrectName());
        
        if (internalRenameMode.getSelectedItem() == ArchiveRenameMode.Same)
          internalPatternField.setText(patternField.getText());
      }
      else if (e.getSource() == internalPatternField && internalRenameMode.getSelectedItem() == ArchiveRenameMode.Custom)
      {
        getRomset().getSettings().internalRenamingPattern = internalPatternField.getText();

      }
    }
    
    @Override
    public String getTitle() { return "Renamer"; }
  }
}
