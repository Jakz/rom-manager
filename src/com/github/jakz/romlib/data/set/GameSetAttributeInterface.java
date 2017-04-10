package com.github.jakz.romlib.data.set;

public interface GameSetAttributeInterface
{
  <T> void setAttribute(GameSetAttribute attrib, T value);
  <T> T getAttribute(GameSetAttribute attrib);
  
  default void setName(String name) { setAttribute(GameSetAttribute.NAME, name); }
  default String getName() { return getAttribute(GameSetAttribute.NAME); }
  
  default void setDescription(String description) { setAttribute(GameSetAttribute.DESCRIPTION, description);  }
  default String getDescription() { return getAttribute(GameSetAttribute.DESCRIPTION); }
}
