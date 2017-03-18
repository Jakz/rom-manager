package jack.rm.script;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomStatus;

public class FindStatement implements Statement
{
  private final Predicate<Rom> query;
  FindStatement(Predicate<Rom> query) { this.query = query; }
  
  public void execute(ScriptEnvironment env)
  {
    List<Rom> roms = env.set.list.stream().filter(query).collect(Collectors.toList());
  
    env.out.append("Find found "+roms.size()+" elements:");
    
    for (Rom r : roms)
    {
      if (r.status == RomStatus.MISSING)
    
        env.out.append("  "+r.getTitle());
      else
        env.out.append("  "+r.getTitle()+" ==> "+env.set.getSettings().romsPath.relativize(r.getHandle().path()));
    }   
  }
}