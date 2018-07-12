package jack.rm.gui.gamelist;

import java.awt.Color;
import java.awt.Component;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import com.github.jakz.romlib.data.game.Drawable;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;

public class GameTreeNode
{
  public static class RootNode implements TreeNode
  {
    private final List<CloneNode> clones;
    
    public RootNode(Stream<GameClone> clones)
    {
      this.clones = clones
        .map(clone -> new CloneNode(this, clone))
        .collect(Collectors.toList());
    }
    
    @Override public boolean getAllowsChildren() { return true; }
    @Override public Enumeration<? extends TreeNode> children() { return Collections.enumeration(clones); }
    @Override public TreeNode getChildAt(int i) { return clones.get(i); }
    @Override public int getChildCount() { return clones.size(); }
    @Override public int getIndex(TreeNode node) { return clones.indexOf(node); }
    @Override public TreeNode getParent() { return null; }
    @Override public boolean isLeaf() { return false; }
  }
  
  public static class CloneNode implements TreeNode
  {
    private final RootNode parent;
    private final List<GameNode> games;
    public final GameClone clone;
    
    public CloneNode(RootNode parent, GameClone clone)
    {
      this.parent = parent;
      this.clone = clone;
      games = clone.stream()
        .map(g -> new GameNode(this, g))
        .collect(Collectors.toList());
    }
    
    @Override public boolean getAllowsChildren() { return true; }
    @Override public Enumeration<? extends TreeNode> children() { return Collections.enumeration(games); }
    @Override public TreeNode getChildAt(int i) { return games.get(i); }
    @Override public int getChildCount() { return games.size(); }
    @Override public int getIndex(TreeNode node) { return games.indexOf(node); }
    @Override public TreeNode getParent() { return parent; }
    @Override public boolean isLeaf() { return false; }
  }
  
  public static class GameNode implements TreeNode
   {
     private final CloneNode parent;
     public final Game game;
     
     public GameNode(CloneNode parent, Game game)
     {
       this.parent = parent;
       this.game = game;
     }
     
     @Override public boolean getAllowsChildren() { return false; }
     @Override public Enumeration<? extends TreeNode> children() { return null; }
     @Override public TreeNode getChildAt(int i) { return null; }
     @Override public int getChildCount() { return 0; }
     @Override public int getIndex(TreeNode node) { return 0; }
     @Override public TreeNode getParent() { return parent; }
     @Override public boolean isLeaf() { return true; }
   }
  
  
  public static class Renderer extends DefaultTreeCellRenderer
  {    
    @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {

      
      JLabel label = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      
      if (!(value instanceof RootNode))
      {
        Drawable drawable = value instanceof GameNode ? (Game)((GameNode)value).game : (GameClone)((CloneNode)value).clone;
            
        label.setIcon(null);
        GameCellRenderer.decorateLabel(label, drawable);
        label.setForeground(drawable.getDrawableStatus().color);
      }
      
      /*else if (value instanceof RomNode)
      {
        Rom rom = (Rom)((RomNode)value).getUserObject();
        label.setText(rom.name+" ["+StringUtils.humanReadableByteCount(rom.size())+"]");
        label.setIcon(Icon.ROM.getIcon());
      }*/

      return label;
    }
  }
}
