package jack.rm.data.rom;

public enum Genre
{
	PLATFORM("Platform"),
	ACTION_PLATFORM("Action Platform"),
	ACTION("Action"),
	ADVENTURE("Adventure"),
	RACING("Racing"),
	FIGHTING("Fighting"),
	BEAM_EM_UP("Beat'em Up"),
	SHOOT_EM_UP("Shoot'em Up"),
	ACTION_RPG("Action RPG"),
	SIMULATION_RPG("Simulation RPG"),
	RPG("RPG"),
	STRATEGY("Strategy"),
	SIMULATION("Simulation"),
	ACTION_PUZZLE("Action Puzzle"),
	PUZZLE("Puzzle"),
	OTHER("Other"),
	MINIGAMES("Minigames"),
	PINBALL("Pinball"),
	FPS("FPS"),
	RTS("RTS"),
	TURN_RPG("Turn RPG"),
	TURN_STRATEGY("Turn Strategy"),
	SPORTS("Sports"),
	MUSIC("Music"),
	BOARD_GAME("Board Game"),
	CARDS("Cards")
	;

  Genre(String name) { this.name = name; }
  
  public final String name;
  
  public String toString() { return name; }
  
  public static Genre forName(String string)
  {
    for (Genre g : values())
    {
      if (g.name.toLowerCase().equals(string.toLowerCase()))
        return g;
    }
    
    return null;
  }
}
