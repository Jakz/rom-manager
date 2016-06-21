package jack.rm.data.search;

import java.util.List;
import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public abstract class SearchParser
{
  abstract Predicate<Rom> parse(List<SearchPredicate> predicates, String string);
  
  private Predicate<Rom> buildSinglePredicate(List<SearchPredicate> predicates, String token)
  {
    for (SearchPredicate predicate : predicates)
    {
      Predicate<Rom> pred = predicate.buildPredicate(token);
      if (pred != null)
        return pred;
    }
    
    return null;
  }
}
