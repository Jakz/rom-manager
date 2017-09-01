package jack.rm.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.functional.StreamUtil;
import com.pixbits.lib.lang.Pair;

import jack.rm.i18n.Text;

public class ViewMenu extends JMenu
{
  private JCheckBoxMenuItem[] filterByStatus;
  
  private JRadioButtonMenuItem[] sortCriteria;
  private ButtonGroup sortCriteriaRadioGroup;
  
  private JCheckBoxMenuItem reverseSortOrder;
  
  private ActionListener listener;
  
  private final Mediator mediator;
  
  ViewMenu(Mediator mediator)
  {
    super(Text.MENU_VIEW_TITLE.text());
    this.mediator = mediator;
    
    listener = e -> {
      if (Arrays.stream(filterByStatus).anyMatch(m -> m == e.getSource()))
        mediator.rebuildGameList();
    };
  }
  
  void clear()
  {
    for (JMenuItem item : filterByStatus) item.removeActionListener(listener);
    for (JMenuItem item : sortCriteria) item.removeActionListener(listener);
    reverseSortOrder.removeActionListener(listener);
    
    this.removeAll();
  }
  
  void rebuild(GameSet set)
  {
    if (set != null)
    {
      final int firstStroke = KeyEvent.VK_F1; // TODO: mind that there's no check for overlap
      final GameStatus[] statuses = GameStatus.values();
      filterByStatus = new JCheckBoxMenuItem[statuses.length];
      
      for (int i = 0; i < filterByStatus.length; ++i)
      {
        filterByStatus[i] = new JCheckBoxMenuItem("Show "+statuses[i].name.toLowerCase(), true);
        filterByStatus[i].setAccelerator(KeyStroke.getKeyStroke(firstStroke + i, 0));
      }
    }
  }
  
  Predicate<Game> buildPredicate()
  {
    final GameStatus[] statuses = GameStatus.values();
    
    /* for each status reduce to a final predicate of the form (game == STATUS && item selected) || ... */
    final BiFunction<Predicate<Game>, Pair<GameStatus, JCheckBoxMenuItem>, Predicate<Game>> accumulator = 
        (p, d) -> p.or(game -> d.first == game.getStatus() && d.second.isSelected());
    
    Predicate<Game> predicate = StreamUtil.zip(statuses, filterByStatus).reduce(
        g -> true, 
        accumulator,
        Predicate::or
    );

    return predicate;
  }
}
