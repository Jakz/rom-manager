package jack.rm.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import jack.rm.i18n.Text;

public enum MenuElement {
  ROMS_SCAN_FOR_ROMS(Text.MENU_ROMS_SCAN_FOR_ROMS, false),
  ROMS_SCAN_FOR_NEW_ROMS(Text.MENU_ROMS_SCAN_FOR_NEW_ROMS, false),
    
  ROMS_EXPORT_MISSING(Text.MENU_ROMS_EXPORT_MISSING, false),
  ROMS_EXPORT_FOUND(Text.MENU_ROMS_EXPORT_FOUND, false),
  
  VIEW_SHOW_CORRECT(Text.MENU_VIEW_SHOW_CORRECT, true, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)),
  VIEW_SHOW_UNORGANIZED(Text.MENU_VIEW_SHOW_UNORGANIZED, true, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0)),
  VIEW_SHOW_NOT_FOUND(Text.MENU_VIEW_SHOW_NOT_FOUND, true, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0)),
  
  VIEW_REVERSE_ORDER(Text.MENU_VIEW_REVERSE_ORDER, true),
  
  
  TOOLS_GLOBAL_SETTINGS(Text.MENU_TOOLS_GLOBAL_SETTINGS, false),
  TOOLS_DOWNLOAD_ASSETS(Text.MENU_TOOLS_DOWNLOAD_ASSETS, false),
  TOOLS_PACK_ASSETS(Text.MENU_TOOLS_PACK_ASSETS, false),
  TOOLS_OPTIONS(Text.MENU_TOOLS_OPTIONS, false),
  TOOLS_SHOW_MESSAGES(Text.MENU_TOOLS_SHOW_MESSAGES, true),
  TOOLS_CONSOLE(Text.MENU_TOOLS_CONSOLE, true)
  ;
  
  MenuElement(Text title, boolean checkbox)
  {
    this(title, checkbox, null);
  }
  
  MenuElement(Text title, boolean checkbox, KeyStroke keyStroke) {
    if (checkbox) {
      item = new JCheckBoxMenuItem(title.text());
    }
    else {
      item = new JMenuItem(title.text());
    }
    
    item.addActionListener(MenuListener.listener);
    
    if (keyStroke != null)
      item.setAccelerator(keyStroke);
  }
  
  public static void clearListeners()
  {
    for (MenuElement element : MenuElement.values())
    {
      ActionListener[] listeners = Arrays.copyOf(element.item.getActionListeners(), element.item.getActionListeners().length);
      for (ActionListener listener : listeners)
        element.item.removeActionListener(listener);
    }
  }
  
  public static void addListeners()
  {
    for (MenuElement element : MenuElement.values())
      element.item.addActionListener(MenuListener.listener);
  }
  
  public JMenuItem item;
  
  public static MenuElement elementForItem(JMenuItem item)
  {
    for (MenuElement e : values())
      if (item == e.item)
        return e;
    
    return null;
  }
}