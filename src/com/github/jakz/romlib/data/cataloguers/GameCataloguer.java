package com.github.jakz.romlib.data.cataloguers;

import com.github.jakz.romlib.data.game.Game;

@FunctionalInterface
public interface GameCataloguer
{
  public void catalogue(Game game);
  public default void done() { }
}
