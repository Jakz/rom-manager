package jack.rm.gui.gamelist;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.Main;
import jack.rm.gui.FileDropperListener;
import jack.rm.gui.Mediator;

public class GameListPanel extends JPanel
{
  private final Mediator mediator;
  
  private final GameListData data;
  
  private Game lastSelectedGame;
  private int lastSelectedIndex;
  
  final private CardLayout layout;
  
  final private GameListModel gameListModel;
  final private JList<Game> list = new JList<>();
  final private ListListener listListener = new ListListener();
  final private JScrollPane listPane = new JScrollPane(list);
  
  public GameListPanel(Mediator mediator)
  {
    this.mediator = mediator;
    
    data = new GameListData();
    gameListModel = new GameListModel(data);
    
    list.setModel(gameListModel);
    list.setCellRenderer(new GameCellRenderer());
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setLayoutOrientation(JList.VERTICAL);
    list.setFixedCellHeight(16);
    list.setBackground(Color.WHITE);
    list.getSelectionModel().addListSelectionListener(listListener);
    list.setSelectedIndex(0);
        
    list.addMouseListener(
        new MouseAdapter(){
          @Override
          public void mouseClicked(MouseEvent e){
            if (e.getClickCount() == 2){
              int r = list.getSelectedIndex();
              
              if (r != -1)
              {
                Game rom = list.getModel().getElementAt(r);
                
                rom.setFavourite(!rom.isFavourite());
                gameListModel.fireChanges(r);   
              }
            }
          }
        });

    listPane.setPreferredSize(new Dimension(230,500));    
    
    
    listPane.setTransferHandler(new FileTransferHandler(new FileDropperListener()));
    
    layout = new CardLayout();
    
    this.setLayout(layout);
    this.add(listPane, "list");
    this.add(new GameTree(), "tree");
  }
  
  public void clearEverything()
  {
    clearSelection();
    setData(Collections.emptyList());
  }
  
  public void backupSelection()
  {
    lastSelectedGame = list.getSelectedValue();
    lastSelectedIndex = list.getSelectedIndex();
  }
  
  public void restoreSelection()
  {
    if (lastSelectedGame != null)     
    {      
      list.clearSelection();
      list.setSelectedValue(lastSelectedGame, true);
      
      if (list.getSelectedValue() == null && lastSelectedIndex != -1)
      {
        list.setSelectedIndex(lastSelectedIndex);
        list.ensureIndexIsVisible(lastSelectedIndex);
      }
    }  
  }
  
  public void sortData(Comparator<Game> sorter) { data.setSorter(sorter); }
  public void filterData(Predicate<Game> predicate) { data.setFilter(predicate); }
  public void setData(List<Game> data) { this.data.setData(data); }
  
  public GameListData data() { return data; }
  public void clearSelection() { list.clearSelection(); }
  public void refresh() { gameListModel.fireChanges(); }
  public void refresh(int row) { gameListModel.fireChanges(row); }
  public void refreshCurrentSelection() { gameListModel.fireChanges(list.getSelectedIndex()); }

  class ListListener implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if (e.getValueIsAdjusting())
        return;

      ListSelectionModel lsm = (ListSelectionModel) e.getSource();

      if (lsm.getMinSelectionIndex() == -1)
      {
        mediator.setInfoPanelContent(null);
        return;
      }

      Game game = gameListModel.getElementAt(lsm.getMinSelectionIndex());

      mediator.setInfoPanelContent(game);
    }
  }
}
