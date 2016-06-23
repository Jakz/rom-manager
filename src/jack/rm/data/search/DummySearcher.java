package jack.rm.data.search;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;

public class DummySearcher extends Searcher
{
  public DummySearcher(RomSet romset)
  {
    
  }
  
  public Predicate<Rom> search(String text)
  {
    return r -> true;
  }
}
