package jack.rm.gui.resources;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.github.jakz.romlib.data.game.GameStatus;

public class Resources
{
  private static final Map<String, Image> images = new HashMap<>();
  private static final Map<String, Icon> icons = new HashMap<>();
  
  public static Image buildSquareIcon(int size, Color fill, Color border)
  {
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.createGraphics();
    g.setColor(fill);
    g.fillRect(0, 0, size - 1, size - 1);
    
    if (border != null)
    {
      g.setColor(border);
      g.drawRect(0, 0, size - 1, size - 1);
    }
    
    return image;
  }
  
  public static void buildStatusImages()
  {
    images.put("status_badly_named.png", buildSquareIcon(11, GameStatus.UNORGANIZED.color, Color.black));
    images.put("status_correct.png", buildSquareIcon(11, GameStatus.FOUND.color, Color.black));
    images.put("status_incomplete.png", buildSquareIcon(11, GameStatus.INCOMPLETE.color, Color.black));
    images.put("status_not_found.png", buildSquareIcon(11, GameStatus.MISSING.color, Color.black));
    images.put("status_all.png", buildSquareIcon(11, new Color(255, 244, 214), Color.black));
  }

  public static Image getImage(String name)
  {
    return images.computeIfAbsent(name, key -> {
      try
      {
        return ImageIO.read(Resources.class.getResource(key));
      }
      catch (IOException e)
      {
        return null;
      }
    });
  }
  
  public static Icon getIcon(String name)
  {
    return icons.computeIfAbsent(name, key -> {
      return new ImageIcon(Resources.getImage(key));
    });
  }
  
  
  static
  {
    buildStatusImages();
  }
  
  
  private static final Icon ICON_STATUS_BADLY_NAMED = getIcon("status_badly_named.png");
  private static final Icon ICON_STATUS_CORRECT = getIcon("status_correct.png");
  private static final Icon ICON_STATUS_INCOMPLETE = getIcon("status_incomplete.png");
  private static final Icon ICON_STATUS_NOT_FOUND = getIcon("status_not_found.png");
  public static final Icon ICON_STATUS_ALL = getIcon("status_all.png");
  
  public static final EnumMap<GameStatus, Icon> statusIcons = new EnumMap<>(GameStatus.class);
  static
  {
    statusIcons.put(GameStatus.FOUND, ICON_STATUS_CORRECT);
    statusIcons.put(GameStatus.UNORGANIZED, ICON_STATUS_BADLY_NAMED);
    statusIcons.put(GameStatus.MISSING, ICON_STATUS_NOT_FOUND);
    statusIcons.put(GameStatus.INCOMPLETE, ICON_STATUS_INCOMPLETE);
  }
}
