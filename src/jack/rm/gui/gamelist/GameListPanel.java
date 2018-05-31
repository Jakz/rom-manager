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

import com.github.jakz.romlib.data.game.Drawable;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.ui.FileTransferHandler;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.gui.FileDropperListener;
import jack.rm.gui.Mediator;

public class GameListPanel extends JPanel
{
  private final Mediator mediator;
  
  private final GameListData data;
  
  private Drawable lastSelectedGame;
  private int lastSelectedIndex;
  
  final private CardLayout layout;
  
  final private GameListModel gameListModel;
  
  final private JList<Drawable> list = new JList<>();
  final private ListListener listListener = new ListListener();
  final private JScrollPane listPane = new JScrollPane(list);
  
  private boolean isTreeMode = false;
  final private GameTree tree;
  final private JScrollPane treePane;
  
  public GameListPanel(Mediator mediator)
  {
    this.mediator = mediator;
    
    data = new GameListData(mediator.preferences().gameListViewMode);
    gameListModel = new GameListModel(data);
    tree = new GameTree(mediator, data);
    treePane = new JScrollPane(tree);
    
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
                Drawable entry = list.getModel().getElementAt(r);
                
                //TODO: better design
                if (entry instanceof Game)
                {
                  Game game = (Game)entry;
                  game.setFavourite(!game.isFavourite());
                  gameListModel.fireChanges(r);   
                }
              }
            }
          }
        });

    listPane.setPreferredSize(new Dimension(230,500));    
    
    
    listPane.setTransferHandler(new FileTransferHandler(new FileDropperListener()));
    
    layout = new CardLayout();
    
    this.setLayout(layout);
    this.add(listPane, "list");
    this.add(treePane, "tree");
  }
  
  public void clearEverything()
  {
    clearSelection();
    setData(Collections.emptyList(), Collections.emptyList());
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
  
  public void selectGameIfVisible(Game game)
  {
    list.setSelectedValue(game, true);
  }
  
  public void sortData(Comparator<? super Drawable> sorter) { data.setSorter(sorter); }
  public void filterData(Predicate<Drawable> predicate) { data.setFilter(predicate); }
  public void setData(List<Game> games, List<GameClone> clones) { this.data.setData(games, clones); }
  
  public GameListData data() { return data; }
  
  public void clearSelection() 
  {
    tree.clearSelection();
    list.clearSelection(); 
  }
  
  public void refresh()
  { 
    gameListModel.fireChanges();
    tree.fireChanges();
  }
  
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

      Drawable entry = gameListModel.getElementAt(lsm.getMinSelectionIndex());

      //TODO: better design
      if (entry instanceof Game)
        mediator.setInfoPanelContent((Game)entry);
      else if (entry instanceof GameClone)
      {
        MyGameSetFeatures helper = Main.current.helper();
        Game game = ((GameClone)entry).getBestMatchForBias(helper.settings().bias, true);
        mediator.setInfoPanelContent(game);
      }
    }
  }
  
  public GameListData.Mode getDataMode() { return data.getMode(); }
  public void setDataMode(GameListData.Mode mode) { data.setMode(mode); }
  
  public boolean isTreeMode() { return isTreeMode; }
  public void setTreeMode(boolean isTreeMode)
  { 
    layout.show(this, isTreeMode ? "tree" : "list");
    this.isTreeMode = isTreeMode;
  }
}
