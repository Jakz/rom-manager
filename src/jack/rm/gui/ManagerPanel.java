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
import java.io.File;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.RomSize.PrintStyle;
import com.github.jakz.romlib.data.game.RomSize.PrintUnit;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.ArchiveFormat;
import com.pixbits.lib.io.archive.Scanner;
import com.pixbits.lib.ui.elements.BrowseButton;

import jack.rm.Settings;
import jack.rm.i18n.Text;

public class ManagerPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	public JLabel romsPathLabel;
	public BrowseButton romsPathButton;
	
	private final InfoTableModel model;
	private final JTable infoTable;
	
	private class InfoTableModel extends AbstractTableModel
	{
	  private long totalSize;
	  private long totalUncompressedSize;
	  
	  
	  private class InfoRow<T>
	  {
	    final String label;
	    final Supplier<T> lambda;
	    
	    InfoRow(String label, Supplier<T> lambda)
	    {
	      this.label = label;
	      this.lambda = lambda;
	      
	      totalSize = 0;
	      totalUncompressedSize = 0;
	    }
	  };

	  private final InfoRow<?>[] rows;
	  
	  InfoTableModel()
	  {
	    rows = new InfoRow<?>[] {
	      new InfoRow<String>("Provider", () -> GameSet.current.info().getName()),
	      new InfoRow<Platform>("System", () -> GameSet.current.platform),
	      new InfoRow<String>("Count", () -> GameSet.current.gameCount() + " roms"),
	      new InfoRow<String>("Owned", () -> GameSet.current.status().getFoundCount() + " roms"),
        new InfoRow<String>("% Complete", () -> { 
          int total = GameSet.current.gameCount();
          int owned = GameSet.current.status().getFoundCount();
          float percent = Math.round((owned / (float)total) * 100);     
          return String.format("%2.0f", percent) + "%";
        }),
        new InfoRow<String>("Total Size", () -> {
          return RomSize.toString(totalSize, PrintStyle.LONG, PrintUnit.BYTES);
        }),
        new InfoRow<String>("Uncompressed Size", () -> {
          return RomSize.toString(totalUncompressedSize, PrintStyle.LONG, PrintUnit.BYTES);
        })

	    };
	  }
	  
    @Override public int getRowCount() { return rows.length; }
    @Override public int getColumnCount() { return 2; }
    @Override public String getColumnName(int c) { return ""; }
    @Override public Class<?> getColumnClass(int c) { return String.class; }
    @Override public Object getValueAt(int r, int c) { return c == 0 ? rows[r].label : rows[r].lambda.get().toString(); }
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
	
	public ManagerPanel()
	{
		romsPathLabel = new JLabel(Text.ROMSET_ROMS_PATH.text());
		
		romsPathButton = new BrowseButton(30, BrowseButton.Type.FILES_AND_DIRECTORIES);
		romsPathButton.setFilter(ArchiveFormat.getReadableMatcher(), "Romsets");
		romsPathButton.setCallback(p -> {
      GameSet.current.getSettings().romsPath = p;
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
	}
	
	public void u(GridBagConstraints c, int x, int y, int w, int h)
	{
		c.gridx = x; c.gridy = y; c.gridwidth = w; c.gridheight = h;
	}
	
	public void updateFields()
	{
		Settings s = GameSet.current.getSettings();
		
    /*if (RomSet.current != null)
    {
      model.totalSize = 0;
      model.totalUncompressedSize = 0;
      RomSet.current.list.stream().filter(r -> r.status != RomStatus.MISSING).map(r -> r.getPath()).forEach(p -> {
        model.totalSize += p.size();
        model.totalUncompressedSize += p.uncompressedSize();
      });
    }*/
		
		if (s.romsPath != null)
		  romsPathButton.setPath(s.romsPath);
	}
}
