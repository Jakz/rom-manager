package jack.rm.gui;

import javax.swing.JMenuItem;

public class TaggedMenuItem<T extends JMenuItem>
{
  final public MenuElement tag;
  public JMenuItem item;
  
  public TaggedMenuItem(String name, MenuElement tag, Class<T> clazz)
  {
    this.tag = tag;
    
    try {
      this.item = clazz.newInstance();
    } catch (Exception e) { e.printStackTrace(); }

    this.item.setText(name);
  }
}
