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

import com.github.jakz.romlib.data.game.Drawable;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.functional.StreamUtil;
import com.pixbits.lib.lang.Pair;

import jack.rm.gui.gamelist.GameListData;
import jack.rm.gui.resources.Resources;
import jack.rm.i18n.Text;

public class ViewMenu extends JMenu
{
  private final Attribute[] sortAttributes = new Attribute[] { GameAttribute.TITLE, GameAttribute.ORDINAL, GameAttribute.SIZE, GameAttribute.NUMBER };
  private final List<Comparator<? super Drawable>> sorters = Arrays.asList(
     null,
     (g1, g2) -> g1.getDrawableCaption().compareToIgnoreCase(g2.getDrawableCaption()),
     (g1, g2) -> Integer.compare(g1.getDrawableOrdinal(), g2.getDrawableOrdinal()),
     (g1, g2) -> Long.compare(g1.getDrawableSize(), g2.getDrawableSize()),
     (g1, g2) -> Integer.compare(((Game)g1).getAttribute(GameAttribute.NUMBER), ((Game)g2).getAttribute(GameAttribute.NUMBER))
  );

  
  private final JCheckBoxMenuItem[] filterByStatus;
  
  private JRadioButtonMenuItem[] sortCriteria;
  private ButtonGroup sortCriteriaRadioGroup;
  
  private JRadioButtonMenuItem[] viewModes;
  private ButtonGroup viewModesRadioGroup;
  
  private JCheckBoxMenuItem reverseSortOrder;
  private JCheckBoxMenuItem showTotalsInCount;
  
  private final ActionListener listener;
  
  private final Mediator mediator;
  
  ViewMenu(Mediator mediator)
  {
    super(Text.MENU_VIEW_TITLE.text());
    this.mediator = mediator;
    this.filterByStatus = new JCheckBoxMenuItem[GameStatus.values().length];
    this.sortCriteria = new JRadioButtonMenuItem[0];
    
    this.showTotalsInCount = new JCheckBoxMenuItem("Show totals in count", mediator.preferences().showTotalsInCountPanel);
    this.showTotalsInCount.addActionListener(e -> {
      mediator.preferences().showTotalsInCountPanel = showTotalsInCount.getState();
      mediator.refreshGameListCounters();
    });
    
    listener = e -> {
      this.mediator.rebuildGameList();
    };
  }
  
  void clear()
  {
    this.removeAll();
  }
  
  void rebuild(GameSet set, GameListData.Mode mode, boolean isTreeMode)
  {
    if (set != null)
    {
      final int firstStroke = KeyEvent.VK_F1; // TODO: mind that there's no check for overlap
      final GameStatus[] statuses = GameStatus.values();
      
      for (int i = 0; i < filterByStatus.length; ++i)
      {
        GameStatus status = statuses[i];
        
        filterByStatus[i] = new JCheckBoxMenuItem("Show "+status.name.toLowerCase(), Resources.statusIcons.get(status), true);
        filterByStatus[i].setAccelerator(KeyStroke.getKeyStroke(firstStroke + i, 0));
        filterByStatus[i].addActionListener(e -> {
          mediator.preferences().setStatusVisibility(status, ((JCheckBoxMenuItem)e.getSource()).isSelected());
          mediator.rebuildGameList();
        });
        add(filterByStatus[i]);
      }
      
      if (set.hasFeature(Feature.CLONES))
      {
        JMenu modeMenu = new JMenu("Mode");
        
        viewModes = new JRadioButtonMenuItem[3];
        viewModes[0] = new JRadioButtonMenuItem("Games");
        viewModes[1] = new JRadioButtonMenuItem("Clones");
        viewModes[2] = new JRadioButtonMenuItem("Clones Tree");
        
        viewModesRadioGroup = new ButtonGroup();
        viewModesRadioGroup.add(viewModes[0]);
        viewModesRadioGroup.add(viewModes[1]);
        viewModesRadioGroup.add(viewModes[2]);
        
        if (isTreeMode)
          viewModes[2].setSelected(true);
        else if (mode == GameListData.Mode.CLONES)
          viewModes[1].setSelected(true);
        else
          viewModes[0].setSelected(true);;

        modeMenu.add(viewModes[0]);
        modeMenu.add(viewModes[1]);
        modeMenu.add(viewModes[2]);
        
        ActionListener switchModeListener = e -> {
          GameListData.Mode m = e.getSource() == viewModes[0] ? GameListData.Mode.GAMES : GameListData.Mode.CLONES;
          boolean treeMode = e.getSource() == viewModes[2];
          mediator.switchGameListMode(m, treeMode);
        };
        
        viewModes[0].addActionListener(switchModeListener);
        viewModes[1].addActionListener(switchModeListener);
        viewModes[2].addActionListener(switchModeListener);
        
        
        add(modeMenu);
        
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
      addSeparator();
      add(showTotalsInCount);
    }
  }
  
  void refreshGameStatusCheckboxes()
  {
    final GameStatus[] statuses = GameStatus.values();

    for (int i = 0; i < statuses.length; ++i)
    {
      filterByStatus[i].setSelected(mediator.preferences().isStatusVisibile(statuses[i]));
    }
  }
  
  Comparator<? super Drawable> buildSorter()
  {
    Comparator<? super Drawable> sorter = null;
    
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
        (p, d) -> p.or(game -> d.first == game.getDrawableStatus() && d.second.isSelected());
    
    Predicate<Game> predicate = StreamUtil.zip(statuses, filterByStatus).reduce(
        g -> false, 
        accumulator,
        Predicate::or
    );

    return predicate;
  }
}
