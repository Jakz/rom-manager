package com.github.jakz.romlib.json;

import com.github.jakz.romlib.data.game.attributes.Attribute;

public class GameSavedAttribute
{
  Attribute key;
  Object value;
  
  GameSavedAttribute() { }
  
  GameSavedAttribute(Attribute key, Object value)
  {
    this.key = key;
    this.value = value;
  }
}
