package jack.rm.gui.gamelist;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import com.github.jakz.romlib.data.game.Drawable;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.LocationSet;
import com.github.jakz.romlib.ui.Icon;

public class GameCellRenderer extends JPanel implements ListCellRenderer<Drawable>
{
  private static final long serialVersionUID = 1L;

  private final JLabel mainLabel = new JLabel();
  private final JLabel rightIcon = new JLabel();

  public GameCellRenderer()
  {
    setOpaque(true);

    mainLabel.setFont(UIManager.getFont("List.font").deriveFont(Font.PLAIN,12));

    setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    rightIcon.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

    add(mainLabel);
    add(rightIcon);
  }

  private void decorate(Drawable entry, boolean isSelected, Color bg)
  {
    decorateLabel(mainLabel, entry);

    GameStatus status = entry.getDrawableStatus();

    if (entry.getDrawableFavourite())
      rightIcon.setIcon(Icon.FAVORITE.getIcon());
    else
      rightIcon.setIcon(null);

    if (isSelected)
    {
      mainLabel.setForeground(UIManager.getColor("List.background"));
      setBackground(status.color);
    }
    else
    {
      setBackground(bg);
      mainLabel.setForeground(status.color);
    }

  }

  @Override
  public Component getListCellRendererComponent(JList<? extends Drawable> list, Drawable entry, int index, boolean iss, boolean chf)
  {
    decorate(entry, iss, list.getBackground());
    return this;
  }
  
  public static void decorateLabel(JLabel label, Drawable entry)
  {
    label.setText(entry.getDrawableCaption());
    LocationSet location = entry.getDrawableLocation();

    if (location != null && location.getIcon() != null)
      label.setIcon(location.getIcon().getIcon());
    else
      label.setIcon(null);
  }  
}