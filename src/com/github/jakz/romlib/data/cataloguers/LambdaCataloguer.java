package com.github.jakz.romlib.data.cataloguers;

import com.github.jakz.romlib.data.game.Game;

@FunctionalInterface
public interface LambdaCataloguer
{
  public boolean catalogue(String token, Game game);
}
