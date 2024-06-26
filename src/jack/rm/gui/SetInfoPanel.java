package jack.rm.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.Rom.Hash;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.RomSize.PrintStyle;
import com.github.jakz.romlib.data.game.RomSize.PrintUnit;
import com.github.jakz.romlib.data.set.CloneSet;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.ArchiveFormat;
import com.pixbits.lib.ui.elements.BrowseButton;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.data.romset.Settings;
import jack.rm.i18n.Text;

public class SetInfoPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public JLabel romsPathLabel;
	public BrowseButton romsPathButton;
	
	private final InfoTableModel model;
	private final JTable infoTable;
	
	private GameSet set;
	
	private class InfoTableModel extends AbstractTableModel
	{	  
	  private class InfoRow<T>
	  {
	    final String label;
	    final Supplier<T> lambda;
	    
	    
	    InfoRow(String label, Supplier<T> lambda)
	    {
	      this.label = label;
	      this.lambda = lambda; 
	    }
	  };

	  private final InfoRow<?>[] rows;
	  private final Map<Integer, Object> cache;
	  
	  public void purgeCache() { cache.clear(); }
	  
	  InfoTableModel()
	  {
	    cache = new HashMap<>();
	    
	    rows = new InfoRow<?>[] {
	      new InfoRow<String>("Provider", () -> set.info().getName()),
	      new InfoRow<String>("System", () -> set.platform().fullName()),
	      new InfoRow<String>("Identifier", () -> set.ident()),
	      new InfoRow<String>("Game Count", () -> set.info().gameCount() + " games"),
	      new InfoRow<String>("Unique Game Count", () -> 
	        String.format("%d games (%.2f per clone)", 
	          set.info().uniqueGameCount(), 
	          (float)set.info().gameCount()/set.info().uniqueGameCount() 
	        )
	      ),
	      new InfoRow<String>("Rom Count", () -> set.info().romCount() + " roms"),
	      new InfoRow<String>("Shared Rom Count", () -> {
	        Map<Hash, List<Rom>> unique = set.romStream().collect(Collectors.groupingBy(rom -> rom.hash()));
	        
	        return Long.toString(unique.entrySet().stream().filter(e -> e.getValue().size() > 1).count()) + " roms";
	      }),
	      new InfoRow<String>("Owned", () -> set.status().getFoundCount() + " roms"),
        new InfoRow<String>("% Complete", () -> { 
          int total = set.gameCount();
          int owned = set.status().getFoundCount();
          float percent = Math.round((owned / (float)total) * 100);     
          return String.format("%2.0f", percent) + "%";
        }),
        new InfoRow<String>("Total Size", () -> {
          return RomSize.toString(set.info().sizeInBytes(), PrintStyle.LONG, PrintUnit.BYTES);
        }),
        new InfoRow<String>("Total Unique Size", () -> {
          Map<Hash, List<Rom>> unique = set.romStream().collect(Collectors.groupingBy(rom -> rom.hash()));
          long size = unique.keySet().stream().mapToLong(h -> h.size()).sum();         
          return RomSize.toString(size, PrintStyle.LONG, PrintUnit.BYTES);
        }),
        new InfoRow<String>("Estimate Size per bias", () -> {
          /* TODO: here we're assuming that there are no orphaned games for cloneset, this should be
          intended behavior right? */
          CloneSet clones = set.clones();
          
          long averageSizeOfAllClones = 0;
            
          if (clones != null)
          {
  	          clones.stream().mapToLong(clone -> {
  	            return (long)clone.stream().mapToLong(Game::getSizeInBytes).average().getAsDouble();
  	          }).sum();
          }
          
          long orphanedGamesSize = set.stream().filter(game -> game.getClone() == null).mapToLong(Game::getSizeInBytes).sum();
          
          return RomSize.toString(averageSizeOfAllClones + orphanedGamesSize, PrintStyle.LONG, PrintUnit.BYTES);       
	      }),
        new InfoRow<String>("Actual Size", () -> {
          long bytes = set.status().foundBytes();
          long cbytes = set.status().compressedBytes();
          return String.format("%s (%s)",
              RomSize.toString(cbytes, PrintStyle.SHORT, PrintUnit.BYTES),
              RomSize.toString(bytes, PrintStyle.SHORT, PrintUnit.BYTES),
              cbytes / (float)bytes
          );
        }),
        new InfoRow<String>("Multiple roms per game?", () -> {
          return set.hasFeature(Feature.SINGLE_ROM_PER_GAME) ? "no" : "yes";
        }),
        new InfoRow<String>("Has finite size set?", () -> set.hasFeature(Feature.FINITE_SIZE_SET) ? "yes" : "no"),

        //new InfoRow<String>("Dat Format", () -> set.info().getFormat().getIdent())
	    };
	  }
	  
    @Override public int getRowCount() { return set != null ? rows.length : 0; }
    @Override public int getColumnCount() { return 2; }
    @Override public String getColumnName(int c) { return ""; }
    @Override public Class<?> getColumnClass(int c) { return String.class; }
    @Override public Object getValueAt(int r, int c)
    { 
      if (c == 0)
        return rows[r].label;
      else
      {
        Object object = cache.computeIfAbsent(r, row -> rows[row].lambda.get());
        return object != null ? object.toString() : null;
      }
    }
      
    @Override public boolean isCellEditable(int r, int c) { return false; }
	}
	
	private class InfoCellRenderer implements TableCellRenderer
	{
	  private final TableCellRenderer renderer;
	  private final int rows;
	  
	  private final Border nonFinalBorder, finalBorder;
	  
	  InfoCellRenderer(TableCellRenderer renderer, int rows)
	  {
	    this.renderer = renderer;
	    this.rows = rows;
	    
	    nonFinalBorder = BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180,180,180)), BorderFactory.createEmptyBorder(2, 5, 1, 5));
	    finalBorder = BorderFactory.createEmptyBorder(2, 5, 2, 5);
	  }
	  
	  @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c)
	  {
	    JLabel label = (JLabel)renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
	    
	    
	    if (c == 0)
	    {
	      label.setHorizontalAlignment(SwingConstants.LEFT);
	      label.setFont(label.getFont().deriveFont(Font.BOLD));
	    }
	    else
	    {
	      label.setHorizontalAlignment(SwingConstants.RIGHT);
	      label.setFont(label.getFont().deriveFont(Font.PLAIN));
	    }
	    
	    if (r < rows-1)
	      label.setBorder(nonFinalBorder);
	    else
	      label.setBorder(finalBorder);

	    return label;
	  }
	}
	
	public SetInfoPanel()
	{
		romsPathLabel = new JLabel(Text.ROMSET_ROMS_PATH.text());
		
		romsPathButton = new BrowseButton(30, BrowseButton.Type.FILES_AND_DIRECTORIES);
		romsPathButton.setFilter(ArchiveFormat.getReadableMatcher(), "Romsets");
		romsPathButton.setCallback(p -> {
		  MyGameSetFeatures helper = Main.current.helper();
		  helper.settings().romsPath = p;
		});
		
		model = new InfoTableModel();
		infoTable = new JTable(model);
		infoTable.setTableHeader(null);
		infoTable.setDefaultRenderer(String.class, new InfoCellRenderer(infoTable.getDefaultRenderer(String.class), model.rows.length));
		
		JScrollPane tablePane = new JScrollPane(infoTable);
		tablePane.setPreferredSize(new Dimension(400,400));
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		u(c,0,0,1,1);
		panel.add(romsPathLabel,c);
		u(c,4,0,1,1);
		panel.add(romsPathButton,c);
		u(c,0,1,5,1);
		c.insets = new Insets(20,0,0,0);
		panel.add(tablePane, c);
		
		this.add(panel);
		
		this.addComponentListener(new ComponentAdapter() {
		  @Override public void componentShown(ComponentEvent event) {
		    model.purgeCache();
		  }
		});
	}
	
	public void u(GridBagConstraints c, int x, int y, int w, int h)
	{
		c.gridx = x; c.gridy = y; c.gridwidth = w; c.gridheight = h;
	}
	
	public void updateFields(GameSet set)
	{
		this.set = set;
		MyGameSetFeatures helper = set.helper();
	  Settings settings = helper.settings();
	  
	  model.purgeCache();
	  model.fireTableDataChanged();
		
		if (settings.romsPath != null)
		  romsPathButton.setPath(settings.romsPath);
		else
		  romsPathButton.clear();
	}
}
