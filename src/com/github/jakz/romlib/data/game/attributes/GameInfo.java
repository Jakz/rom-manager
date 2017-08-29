package com.github.jakz.romlib.data.game.attributes;

import com.github.jakz.romlib.data.game.LanguageSet;
import com.github.jakz.romlib.data.game.LocationSet;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.game.Version;

public class GameInfo extends AttributeSet
{  
  public GameInfo()
  {    
    setAttribute(GameAttribute.LOCATION, new LocationSet());
    setAttribute(GameAttribute.LANGUAGE, new LanguageSet());
    setAttribute(GameAttribute.VERSION, Version.PROPER);
    setAttribute(GameAttribute.LICENSED, true);
  }
}
