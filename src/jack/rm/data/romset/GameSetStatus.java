package jack.rm.data.romset;

import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;

public class GameSetStatus
{
  private int countCorrect;
  private int countNotFound;
  private int countBadlyNamed;
  
  public GameSetStatus()
  {
    countCorrect = 0;
    countNotFound = 0;
    countBadlyNamed = 0;
  }
  
  void refresh(Stream<Game> games)
  {
    countNotFound = 0;
    countBadlyNamed = 0;
    countCorrect = 0;
    
    games.forEach(g -> {
      switch (g.status)
      {
        case MISSING: ++countNotFound; break;
        case UNORGANIZED: ++countBadlyNamed; break;
        case FOUND: ++countCorrect; break;
      }
    });
  }
  
  public int getCorrectCount() { return countCorrect; }
  public int getNotFoundCount() { return countNotFound; }
  public int getUnorganizedCount() { return countBadlyNamed; }
  public int getFoundCount() { return countCorrect + countBadlyNamed; }
}
