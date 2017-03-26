package com.github.jakz.romlib.data.set;

import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;

public class GameSetStatus
{
  private int countCorrect;
  private int countNotFound;
  private int countIncomplete;
  private int countBadlyNamed;
  
  
  public GameSetStatus()
  {
    countCorrect = 0;
    countNotFound = 0;
    countBadlyNamed = 0;
    countIncomplete = 0;
  }
  
  public void refresh(Stream<Game> games)
  {
    countNotFound = 0;
    countBadlyNamed = 0;
    countCorrect = 0;
    
    games.forEach(g -> {
      switch (g.getStatus())
      {
        case MISSING: ++countNotFound; break;
        case INCOMPLETE: ++countIncomplete; break;
        case UNORGANIZED: ++countBadlyNamed; break;
        case FOUND: ++countCorrect; break;
      }
    });
  }
  
  public int getCorrectCount() { return countCorrect; }
  public int getNotFoundCount() { return countNotFound; }
  public int getUnorganizedCount() { return countBadlyNamed; }
  public int getIncompleteCount() { return countIncomplete; }
  public int getFoundCount() { return countCorrect + countBadlyNamed; }
}
