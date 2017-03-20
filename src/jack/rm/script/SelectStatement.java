package jack.rm.script;

import java.util.function.Predicate;

import com.github.jakz.romlib.data.game.Game;

public class SelectStatement implements Statement
{
  private final Predicate<Game> query;
  SelectStatement(Predicate<Game> query) { this.query = query; }
  
  public void execute(ScriptEnvironment env)
  {
    
  }
}