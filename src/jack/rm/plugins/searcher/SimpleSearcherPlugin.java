package jack.rm.plugins.searcher;


import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import jack.rm.data.rom.Rom;
import jack.rm.data.search.SearchParser;
import jack.rm.data.search.SearchPredicate;

public class SimpleSearcherPlugin extends SearcherPlugin
{
  private class SimpleSearcher extends SearchParser
  {

    @Override
    public Function<List<SearchPredicate>, Predicate<Rom>> parse(String string)
    {
      return null;
    }
    
  }
  
  @Override
  public SearchParser getSearcher()
  {

    return null;
  }

}
