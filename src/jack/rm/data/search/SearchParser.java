package jack.rm.data.search;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public abstract class SearchParser
{
  abstract Function<List<SearchPredicate>,Predicate<Rom>> parse(String string);
  
  protected Predicate<Rom> buildSinglePredicate(List<SearchPredicate> predicates, String token)
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
