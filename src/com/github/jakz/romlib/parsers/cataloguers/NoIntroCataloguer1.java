package com.github.jakz.romlib.parsers.cataloguers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;

public class NoIntroCataloguer1 implements GameCataloguer
{
  private List<String> addendums;
  
  public NoIntroCataloguer1()
  {
    addendums = new ArrayList<>();
  }
  
  @Override
  public void catalogue(Game game)
  {
    String title = game.getTitle();
    
    int firstParen = title.indexOf('(');
    
    AtomicBoolean usa = new AtomicBoolean(false);
    AtomicBoolean japan = new AtomicBoolean(false);
    AtomicBoolean europe = new AtomicBoolean(false);
          
    Arrays.stream(title.substring(firstParen).split("\\(|\\)")).filter(s -> !s.isEmpty()).map(s -> s.trim()).forEach(s -> {
      Arrays.stream(s.split(",")).map(t -> t.trim()).filter(t -> !t.isEmpty()).forEach(t -> {
        if (t.equals("USA")) usa.set(true);
        else if (t.equals("Japan")) japan.set(true);
        else if (t.equals("Europe")) europe.set(true);
        else if (t.equals("Korea")) game.getLocation().add(Location.KOREA);
        else if (t.equals("World")) game.getLocation().add(Location.WORLD);
        else if (t.equals("Ja")) game.getLanguages().add(Language.JAPANESE);
        else if (t.equals("Nl")) game.getLanguages().add(Language.DUTCH);
        else if (t.equals("De")) game.getLanguages().add(Language.GERMAN);
        else if (t.equals("No")) game.getLanguages().add(Language.NORWEGIAN);
        else if (t.equals("Sv")) game.getLanguages().add(Language.SWEDISH);
        else if (t.equals("Pt")) game.getLanguages().add(Language.PORTUGUESE);
        else if (t.equals("En")) game.getLanguages().add(Language.ENGLISH);
        else if (t.equals("It")) game.getLanguages().add(Language.ITALIAN);
        else if (t.equals("Es")) game.getLanguages().add(Language.SPANISH);
        else if (t.equals("Fr")) game.getLanguages().add(Language.FRENCH);
        else if (t.equals("Proto") || t.equals("Proto 1") || t.equals("Development Edition") || 
                t.equals("Rev 1") || t.equals("Proto 2") || t.equals("v2.0") || t.equals("Auto Demo") || t.equals("Sample") || t.equals("Beta"))
          game.setAttribute(GameAttribute.VERSION, t);
        else 
        {
          game.setComment(t);
          addendums.add(t);
        }
        
      });
      
        
        
        /*String previous = rom.getAttribute(RomAttribute.COMMENT);
        if (previous == null) previous = "";
        rom.setAttribute(RomAttribute.COMMENT, previous + ", " + s);*/
    });
    
    game.setNormalizedTitle(title.substring(0, firstParen-1));
    
    if (usa.get() && japan.get() && !europe.get())
      game.getLocation().add(Location.USA_JAPAN);
    else if (usa.get() && !japan.get() && europe.get())
      game.getLocation().add(Location.USA_EUROPE);
    else if (!usa.get() && japan.get() && europe.get())
      game.getLocation().add(Location.JAPAN_EUROPE);
    else if (usa.get() && !japan.get() && !europe.get())
      game.getLocation().add(Location.USA);
    else if (!usa.get() && japan.get() && !europe.get())
      game.getLocation().add(Location.JAPAN);
    else if (!usa.get() && !japan.get() && europe.get())
      game.getLocation().add(Location.EUROPE);
  }
  
  public void printAddendums()
  {
    addendums.forEach(t -> System.out.println(t));
  }
}
