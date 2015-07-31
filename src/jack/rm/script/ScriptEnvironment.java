package jack.rm.script;

import jack.rm.data.romset.RomSet;

public class ScriptEnvironment
{
  final ScriptStdout out;
  final RomSet set;
  
  public ScriptEnvironment(RomSet set, ScriptStdout out)
  {
    this.out = out;
    this.set = set;
  }
}
