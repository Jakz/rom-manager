package jack.rm.gui.gameinfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameClone;
import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.gui.Mediator;

class ClonesEnumPanel extends JPanel
{
  private static enum Mode
  {
    COMPACT,
    EXTENDED
  };
  
  private final Mediator mediator;
  
  private Mode mode = Mode.EXTENDED; //Mode.COMPACT;
  private GameSet set;
  
  private Game game;
  private Game clones[];
  
  private final JLabel title;
  private final JPanel inner;
 
  
  public ClonesEnumPanel(Mediator mediator)
  {
    this.mediator = mediator;
    
    this.set = null;
    //this.setPreferredSize(new Dimension(500, 200));
    this.inner = new JPanel();
    this.title = new JLabel();
    this.setLayout(new BorderLayout());
    this.add(title, BorderLayout.NORTH);
    this.add(inner, BorderLayout.CENTER);
    
    this.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
  }
  
  void gameSetLoaded(GameSet set)
  {
    inner.removeAll();
    inner.setLayout(mode == Mode.COMPACT ? new FlowLayout() : new BoxLayout(inner, BoxLayout.PAGE_AXIS));
    this.set = set;
  }
  
  public void update(final Game game)
  {
    Runnable updater = () -> { 
      inner.removeAll();     
      this.game = game;
      
      if (game != null)
      {
        GameClone clones = game.getClone();
        
        if (clones != null)
        {
          this.clones = clones.stream().toArray(i -> new Game[i]);
          //title.setText("Clones: "+this.clones.length);
          
          inner.add(new JLabel(""+this.clones.length+" clones:"));
          
          for (final Game clone : this.clones)
          {
            JButton label = new JButton();
            
            label.setFocusPainted(false);
            label.setContentAreaFilled(false);
            label.setOpaque(false);
            
            label.setIcon(clone.getLocation().getIcon().getIcon());
            label.setText(mode == Mode.COMPACT ? clone.getVersion().toString() : clone.getCorrectName());
            label.setForeground(clone.getStatus().color);
            label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.DARK_GRAY), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            label.setToolTipText(clone.getCorrectName());
            label.addActionListener(e -> { if (game != clone) mediator.selectGameIfVisible(clone); });

            inner.add(label);
          }
          
          revalidate();
          repaint();
          return;
        }
      }
    
      title.setText("");
      revalidate();
      repaint();
      return;
    };
    
    if (SwingUtilities.isEventDispatchThread())
      updater.run();
    else SwingUtilities.invokeLater(updater);
  }
}
