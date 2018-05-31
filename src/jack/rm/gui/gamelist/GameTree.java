package jack.rm.gui.gamelist;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;

import jack.rm.Main;
import jack.rm.data.romset.MyGameSetFeatures;
import jack.rm.gui.Mediator;
import jack.rm.gui.gamelist.GameTreeNode.CloneNode;
import jack.rm.gui.gamelist.GameTreeNode.GameNode;

public class GameTree extends JTree
{
  private GameTreeNode.RootNode root;
  private DefaultTreeModel model;
  private final GameListData data;
  private final Mediator mediator;
  
  GameTree(Mediator mediator, GameListData data)
  {
    model = new DefaultTreeModel(null, true);
    
    this.data = data;
    this.mediator = mediator;
    this.setRootVisible(false);   
    this.setModel(model);
    this.setCellRenderer(new GameTreeNode.Renderer());
  }
  
  public void fireChanges()
  {
    if (data.getMode() == GameListData.Mode.CLONES)
      root = new GameTreeNode.RootNode(data.stream().map(d -> (GameClone)d));
    else
      root = null;
    
    this.addTreeSelectionListener(new Listener());

    model.setRoot(root);
    model.nodeStructureChanged(root);
  }
  
  public void clearSelection()
  {
    super.clearSelection();
  }
  
  private class Listener implements TreeSelectionListener
  {
    @Override
    public void valueChanged(TreeSelectionEvent e)
    {
      TreeNode node = (TreeNode)GameTree.this.getLastSelectedPathComponent();
      
      //TODO: better design
      if (node instanceof GameNode)
        mediator.setInfoPanelContent(((GameNode)node).game);
      else if (node instanceof GameClone)
      {
        MyGameSetFeatures helper = Main.current.helper();
        Game game = ((CloneNode)node).clone.getBestMatchForBias(helper.settings().bias, true);
        mediator.setInfoPanelContent(game);
      }
    }
    
  };
}
