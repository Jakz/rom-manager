package com.github.jakz.romlib.parsers;

import com.github.jakz.romlib.data.game.Game;

@FunctionalInterface
public interface GameCataloguer
{
  public void catalogue(Game game);
}
