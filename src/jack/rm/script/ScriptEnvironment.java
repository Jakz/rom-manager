package jack.rm.script;

import jack.rm.data.romset.GameSet;

public class ScriptEnvironment
{
  final ScriptStdout out;
  final GameSet set;
  
  public ScriptEnvironment(GameSet set, ScriptStdout out)
  {
    this.out = out;
    this.set = set;
  }
}
