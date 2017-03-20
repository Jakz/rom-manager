package com.github.jakz.romlib.data.platforms;

import com.github.jakz.romlib.data.game.GameSize;
import com.github.jakz.romlib.data.game.GameSave;

public class NDS
{
  public static class Save implements GameSave<Save.Type>
  {
    public static enum Type implements GameSave.Type
    {
      EEPROM,
      FLASH,
      TBC,
      NONE
    };
    
    private final Type type;
    private long size;
    
    public Save(Type type)
    {
      this.type = type;
    }

    public Save(Type type, long size)
    {
      this(type);
      this.size = size;
    }
    
    public String toString()
    {
      if (size != 0)
      {
        return String.format("%s (%s)", type.toString(), GameSize.toString(this.size));
      }
      else
        return type.toString();
    }
    
    public Type getType() { return type; }
  }
}
