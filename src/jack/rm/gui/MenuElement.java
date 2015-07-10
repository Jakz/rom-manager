package jack.rm.gui;

import jack.rm.i18n.Text;
import javax.swing.*;

public enum MenuElement {
  ROMS_SCAN_FOR_ROMS(Text.MENU_ROMS_SCAN_FOR_ROMS, false),
  ROMS_SCAN_FOR_NEW_ROMS(Text.MENU_ROMS_SCAN_FOR_NEW_ROMS, false),
  ROMS_EXPORT_MISSING(Text.MENU_ROMS_EXPORT_MISSING, false),
  ROMS_EXPORT_FOUND(Text.MENU_ROMS_EXPORT_FOUND, false),
  ROMS_EXIT(Text.MENU_ROMS_EXIT, false),
  ROMS_RENAME(Text.MENU_ROMS_RENAME, false),
  
  VIEW_SHOW_CORRECT(Text.MENU_VIEW_SHOW_CORRECT, true),
  VIEW_SHOW_NOT_FOUND(Text.MENU_VIEW_SHOW_NOT_FOUND, true),
  VIEW_SHOW_BADLY_NAMED(Text.MENU_VIEW_SHOW_BADLY_NAMED, true),
  
  TOOLS_DOWNLOAD_ART(Text.MENU_TOOLS_DOWNLOAD_ART, false),
  TOOLS_OPTIONS(Text.MENU_TOOLS_OPTIONS, false),
  TOOLS_SHOW_CONSOLE(Text.MENU_TOOLS_SHOW_CONSOLE, true)
  ;
  
  MenuElement(Text title, boolean checkbox) {
    if (checkbox) {
      item = new JCheckBoxMenuItem(title.text());
    }
    else {
      item = new JMenuItem(title.text());
    }

    item.addActionListener(MenuListener.listener);
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