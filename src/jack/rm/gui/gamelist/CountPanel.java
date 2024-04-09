package jack.rm.gui.gamelist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;

import jack.rm.gui.Mediator;
import jack.rm.gui.resources.Resources;

public class CountPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

  private static GameStatus[] statuses = new GameStatus[] {
    GameStatus.FOUND, GameStatus.UNORGANIZED, GameStatus.INCOMPLETE, GameStatus.MISSING
  };
 
	private final Mediator mediator;
	
	private final GameListData data;
	private final JToggleButton[] counters = new JToggleButton[5];
	
	JPanel inner;
	
	
	public CountPanel(Mediator mediator, GameListData data)
	{
    this.mediator = mediator;
	  
	  this.data = data;
	  
	  inner = new JPanel();
		for (int i = 0; i < counters.length; ++i)
		{
		  counters[i] = new JToggleButton("0000");
			counters[i].setBorderPainted(false);
			counters[i].setPreferredSize(new Dimension(70, 14));
			
			if (i < counters.length - 1)
			{
			  GameStatus status = statuses[i];
			  
			  counters[i].setIcon(Resources.statusIcons.get(status));
			  counters[i].addActionListener(e -> {
			    mediator.preferences().setStatusVisibility(status, !((JToggleButton)e.getSource()).isSelected());
			    mediator.refreshGameStatusCheckboxesInViewMenu();
			    mediator.rebuildGameList();
			  });
			}
			else
		    counters[i].setIcon(Resources.ICON_STATUS_ALL);
		}

		this.setLayout(new BorderLayout());
		this.add(inner, BorderLayout.WEST);
	}
	
	public void gameSetLoaded(GameSet set)
	{
	  if (inner != null)
	    inner.removeAll();
	  
	  boolean singleRomPerGame = set.hasFeature(Feature.SINGLE_ROM_PER_GAME);
	  
    for (int i = 0; i < counters.length; ++i)
    {
      //TODO: this should be visible when clones are enabled
      //if (singleRomPerGame && i == 2)
      //  continue;
      inner.add(counters[i]);
    } 
    
    revalidate();
	}
	
	public void update()
	{	  
	  Map<GameStatus, Long> status = data.stream().collect(Collectors.groupingBy( r -> r.getDrawableStatus(), HashMap::new, Collectors.counting()));

	  
    for (int i = 0; i < statuses.length; ++i)
    {
      counters[i].setText(""+status.getOrDefault(statuses[i], 0L));
      counters[i].setSelected(!mediator.preferences().isStatusVisibile(statuses[i]));
    }
	  
    counters[4].setText(""+data.getSize());
    
    boolean showTotals = mediator.preferences().showTotalsInCountPanel;
    
    for (JComponent label : counters)
      label.setPreferredSize(new Dimension(showTotals ? 110 : 70, 14));
    
    if (showTotals)
    {
      Map<GameStatus, Long> totalStatus = data.originalStream().collect(Collectors.groupingBy( r -> r.getDrawableStatus(), HashMap::new, Collectors.counting()));

      counters[0].setText(counters[0].getText()+ " (" + totalStatus.getOrDefault(GameStatus.FOUND, 0L) + ")");
      counters[1].setText(counters[1].getText()+ " (" + totalStatus.getOrDefault(GameStatus.UNORGANIZED, 0L) + ")");
      counters[2].setText(counters[2].getText()+ " (" + totalStatus.getOrDefault(GameStatus.INCOMPLETE, 0L) + ")");
      counters[3].setText(counters[3].getText()+ " (" + totalStatus.getOrDefault(GameStatus.MISSING, 0L) + ")");
      counters[4].setText(counters[4].getText()+ " (" + data.originalStream().count() + ")");
    }
	}
}
