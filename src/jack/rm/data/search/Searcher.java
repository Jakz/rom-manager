package jack.rm.data.search;

import java.util.ArrayList;
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
  
  public static String[] splitWithDelimiter(String token, String delim)
  {
    String[] tokens = token.split(delim);
    
    if (tokens.length == 2)
    {
      if (tokens[1].startsWith("\""))
        tokens[1] = tokens[1].substring(1);
      if (tokens[1].endsWith("\""))
        tokens[1] = tokens[1].substring(0, tokens[1].length()-1);
      
      return tokens;
    }
    
    return tokens;
  }
}
