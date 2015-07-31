package jack.rm.script;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public class SelectStatement implements Statement
{
  private final Predicate<Rom> query;
  SelectStatement(Predicate<Rom> query) { this.query = query; }
  
  public void execute(ScriptEnvironment env)
  {
    
  }
}