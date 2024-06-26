package jack.rm.script;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Game;

public class FindStatement implements Statement
{
  private final Predicate<Game> query;
  FindStatement(Predicate<Game> query) { this.query = query; }
  
  public void execute(ScriptEnvironment env)
  {
    List<Game> roms = env.set.stream().filter(query).collect(Collectors.toList());
  
    env.out.append("Find found "+roms.size()+" elements:");
    
    for (Game r : roms)
    {
      if (!r.getStatus().isComplete())
        env.out.append("  "+r.getTitle());
      else
        env.out.append("  "+r.getTitle()/*TODO: +" ==> "+env.set.getSettings().romsPath.relativize(r.getHandle().path())*/);
    }   
  }
}