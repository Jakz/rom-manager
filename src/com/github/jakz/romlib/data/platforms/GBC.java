package com.github.jakz.romlib.data.platforms;

import com.github.jakz.romlib.data.game.GameSave;
import com.github.jakz.romlib.data.game.attributes.CustomGameAttribute;
import com.github.jakz.romlib.ui.i18n.Text;

public class GBC
{
  public static class Save implements GameSave<Save.Type>
  {
    public static enum Type implements GameSave.Type
    {
      NONE,
    };
    
    private final Type type;
    private final long size;
    
    public Save(Type type, long size)
    {
      this.type = type;
      this.size = size;
    }
    
    public Type getType() { return type; }
    public long getSize() { return size; }
  }
  
  public static class Attribute
  {
    public static final com.github.jakz.romlib.data.game.attributes.Attribute GB_COMPATIBLE = 
        new CustomGameAttribute.Boolean("gb-compatible", Text.ATTRIBUTE_GBC_GB_COMPATIBLE);
    
    public static final com.github.jakz.romlib.data.game.attributes.Attribute SGB_ENHANCED = 
        new CustomGameAttribute.Boolean("sgb-enhanced", Text.ATTRIBUTE_GBC_SGB_ENHANCED);
  }
}
