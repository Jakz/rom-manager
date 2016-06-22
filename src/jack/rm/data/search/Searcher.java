package jack.rm.data.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jack.rm.data.rom.Rom;

public class Searcher
{
  private final SearchParser parser;
  private final List<SearchPredicate> predicates;
  
  Searcher(SearchParser parser)
  {
    this.parser = parser;
    predicates = new ArrayList<>();
  }
    
  Predicate<Rom> buildPredicate(String string)
  {
    return parser.parse(predicates, string);
  }
}
