package com.github.jakz.romlib.data.platforms;

import com.github.jakz.romlib.data.game.GameSave;

public class GB
{
  public static class Save implements GameSave<Save.Type>
  {
    public static enum Type implements GameSave.Type
    {
      NONE,
      SRAM
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
}
