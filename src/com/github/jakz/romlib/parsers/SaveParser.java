package com.github.jakz.romlib.parsers;

import com.github.jakz.romlib.data.game.GameSave;

@FunctionalInterface
public interface SaveParser
{
  public GameSave<?> parse(String string);
}
