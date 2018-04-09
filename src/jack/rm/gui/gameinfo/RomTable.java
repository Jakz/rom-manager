package jack.rm.gui.gameinfo;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.attributes.RomAttribute;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.ColumnVisibilityMenu;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.LambdaLabelTableRenderer;

class RomTable extends JTable
{
  private Game game;
  private TableModel<Rom> model;
  private DataSource<Rom> data;
  
  private ColumnVisibilityMenu menu;
    
  RomTable()
  {
    data = DataSource.of(Collections.emptyList());
    model = new TableModel<Rom>(this, data);
        
    ColumnSpec<Rom, String> nameColumn = new ColumnSpec<>("Name", String.class, r -> r.getAttribute(RomAttribute.ROM_NAME));
    ColumnSpec<Rom, RomSize> sizeColumn = new ColumnSpec<>("Size", RomSize.class, r -> r.getAttribute(RomAttribute.SIZE));
    
    //TODO: enable only if set supports CRC32?
    ColumnSpec<Rom, Long> crcColumn = new ColumnSpec<>("CRC32", Long.class, r -> r.getAttribute(RomAttribute.CRC));
    
    LambdaLabelTableRenderer<Long> crcRenderer = new LambdaLabelTableRenderer<>((v,l) -> l.setText(String.format("%08X", v).toUpperCase()));
    crcColumn.setRenderer(crcRenderer);
    
    LambdaLabelTableRenderer<RomSize> sizeRenderer = new LambdaLabelTableRenderer<>((v,l) -> l.setText(v.toString(RomSize.PrintStyle.SHORT, RomSize.PrintUnit.BYTES)));
    sizeColumn.setRenderer(sizeRenderer);
      
    sizeColumn.setWidth(80);
    crcColumn.setWidth(80);
    
    model.addColumn(sizeColumn);
    model.addColumn(crcColumn);
    model.addColumn(nameColumn);
    
    {
      //TODO: add only if romset supports it
      //TODO: save hidden/shown columns in romset preferences?

      menu = new ColumnVisibilityMenu(sizeColumn, crcColumn);
      
      this.getTableHeader().addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e)
        {
          if (e.isPopupTrigger())
            menu.show(RomTable.this, e.getX(), e.getY());
        }
        
        @Override
        public void mouseReleased(MouseEvent e)
        {
          if (e.isPopupTrigger())
            menu.show(RomTable.this, e.getX(), e.getY());
        }
      });
    }
    
    
  }
  
  void update(Game game)
  {
    this.game = game;
    data = DataSource.of(game.stream().collect(Collectors.toList()));
    model.setData(data);
    model.fireTableDataChanged();
  }
  
  @Override
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
  {
    Component component = super.prepareRenderer(renderer, row, column);
    
    Rom rom = game.rom(row);
    //TODO: GameStatus.UNORGANZIED management for rom?
    component.setForeground(rom.isPresent() ? GameStatus.FOUND.color : GameStatus.MISSING.color);
    
    return component;
  }
  
}
