package jack.rm.plugins.renamer;

import java.util.Set;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import jack.rm.files.Organizer;
import jack.rm.files.Pattern;

public class PatternRenamerPlugin extends RenamerPlugin
{
  @Override public String getCorrectName(Rom rom)
  {
    String temp = new String(RomSet.current.getSettings().renamingPattern);
    
    Set<Pattern> patterns = Organizer.getPatterns(RomSet.current);
    
    for (Pattern p : patterns)
      temp = p.apply(temp, rom);
    
    return temp;
  }
}
