package com.github.jakz.romlib.data.set.organizers;

import com.github.jakz.romlib.data.game.Game;

@FunctionalInterface
public interface GameRenamer
{
  String getNameForGame(Game game);
  
  public static final GameRenamer DUMMY = g -> g.getTitle();
}
