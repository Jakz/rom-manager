package jack.rm.gui;

import javax.swing.ImageIcon;

public enum Icon
{
  FLAG_AUSTRALIA("flag_australia"),
  FLAG_BRAZIL("flag_brazil"),
  FLAG_CHINA("flag_china"),
  FLAG_DENMARK("flag_denmark"),
  FLAG_EUROPE("flag_europe"),
  FLAG_FINLAND("flag_finland"),
  FLAG_FRANCE("flag_france"),
  FLAG_GERMANY("flag_germany"),
  FLAG_ITALY("flag_italy"),
  FLAG_JAPAN("flag_japan"),
  FLAG_KOREA("flag_korea"),
  FLAG_NETHERLANDS("flag_netherlands"),
  FLAG_NORWAY("flag_norway"),
  FLAG_POLAND("flag_poland"),
  FLAG_PORTUGAL("flag_portugal"),
  FLAG_SPAIN("flag_spain"),
  FLAG_SWEDEN("flag_sweden"),
  FLAG_UNITED_KINGDOM("flag_united_kingdom"),
  FLAG_USA("flag_usa"),
  STATUS_ALL("status_all"),
  STATUS_BADLY_NAMED("status_badly_named"),
  STATUS_CORRECT("status_correct"),
  STATUS_NOT_FOUND("status_not_found"),
  FAVORITE("favorite"),
  EDIT("edit"),
  DELETE("delete")

  ;
  
  private final String name;
  private ImageIcon icon;
  
  Icon(String name)
  {
    this.name = name;
  }
  
  ImageIcon getIcon()
  {
    if (icon == null)
      icon = new ImageIcon(this.getClass().getClassLoader().getResource("jack/rm/gui/resources/"+name+".png"));
    
    return icon;
  }
}
