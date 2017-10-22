package jack.rm.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.functional.StreamUtil;
import com.pixbits.lib.lang.Pair;

import jack.rm.i18n.Text;

public class ViewMenu extends JMenu
{
  private final Attribute[] sortAttributes = new Attribute[] { GameAttribute.TITLE, GameAttribute.ORDINAL, GameAttribute.SIZE, GameAttribute.NUMBER };
  private final List<Comparator<Game>> sorters = Arrays.asList(
     null,
     (g1, g2) -> g1.getTitle().compareToIgnoreCase(g2.getTitle()),
     (g1, g2) -> Integer.compare(g1.getAttribute(GameAttribute.ORDINAL), g2.getAttribute(GameAttribute.ORDINAL)),
     (g1, g2) -> Long.compare(g1.getSize().bytes(), g2.getSize().bytes()),
     (g1, g2) -> Integer.compare(g1.getAttribute(GameAttribute.NUMBER), g2.getAttribute(GameAttribute.NUMBER))
  );

  
  private final JCheckBoxMenuItem[] filterByStatus;
  
  private JRadioButtonMenuItem[] sortCriteria;
  private ButtonGroup sortCriteriaRadioGroup;
  
  private JCheckBoxMenuItem reverseSortOrder;
  
  private final ActionListener listener;
  
  private final Mediator mediator;
  
  ViewMenu(Mediator mediator)
  {
    super(Text.MENU_VIEW_TITLE.text());
    this.mediator = mediator;
    this.filterByStatus = new JCheckBoxMenuItem[GameStatus.values().length];
    this.sortCriteria = new JRadioButtonMenuItem[0];
    
    listener = e -> {
      this.mediator.rebuildGameList();
    };
  }
  
  void clear()
  {
    this.removeAll();
  }
  
  void rebuild(GameSet set)
  {
    if (set != null)
    {
      final int firstStroke = KeyEvent.VK_F1; // TODO: mind that there's no check for overlap
      final GameStatus[] statuses = GameStatus.values();
      
      for (int i = 0; i < filterByStatus.length; ++i)
      {
        filterByStatus[i] = new JCheckBoxMenuItem("Show "+statuses[i].name.toLowerCase(), true);
        filterByStatus[i].setAccelerator(KeyStroke.getKeyStroke(firstStroke + i, 0));
        filterByStatus[i].addActionListener(listener);
        add(filterByStatus[i]);
      }
      
      addSeparator();
      
      sortCriteriaRadioGroup = new ButtonGroup();
      sortCriteria = new JRadioButtonMenuItem[sortAttributes.length+1];
      
      JMenu sortMenu = new JMenu(Text.MENU_VIEW_SORT_BY.text());
      
      sortCriteria[0] = new JRadioButtonMenuItem("None");
      for (int i = 0; i < sortAttributes.length; ++i)
        sortCriteria[i+1] = new JRadioButtonMenuItem(sortAttributes[i].getCaption());
      
      for (JRadioButtonMenuItem menuItem : sortCriteria)
      {
        menuItem.addActionListener(listener);
        sortCriteriaRadioGroup.add(menuItem);
        sortMenu.add(menuItem);
      }
      
      sortCriteria[0].setSelected(true);
      
      sortCriteria[3].setEnabled(set.doesSupportAttribute(GameAttribute.NUMBER));
      
      reverseSortOrder = new JCheckBoxMenuItem(Text.MENU_VIEW_REVERSE_ORDER.text());
      reverseSortOrder.addActionListener(listener);
      
      add(sortMenu);
      add(reverseSortOrder); 
    }
  }
  
  Comparator<Game> buildSorter()
  {
    Comparator<Game> sorter = null;
    
    for (int i = 0; i < sortCriteria.length; ++i)
    {
      JRadioButtonMenuItem criteria = sortCriteria[i];
      
      if (criteria.isSelected())
        sorter = sorters.get(i);
    }
    
    return sorter != null && reverseSortOrder.isSelected() ? sorter.reversed() : sorter;
  }
  
  Predicate<Game> buildPredicate()
  {
    final GameStatus[] statuses = GameStatus.values();
    
    /* for each status reduce to a final predicate of the form (game == STATUS && item selected) || ... */
    final BiFunction<Predicate<Game>, Pair<GameStatus, JCheckBoxMenuItem>, Predicate<Game>> accumulator = 
        (p, d) -> p.or(game -> d.first == game.getStatus() && d.second.isSelected());
    
    Predicate<Game> predicate = StreamUtil.zip(statuses, filterByStatus).reduce(
        g -> false, 
        accumulator,
        Predicate::or
    );

    return predicate;
  }
}
