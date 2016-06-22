package jack.rm.data.search;

import java.util.Arrays;
import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public abstract class SearchPredicate
{
  abstract String getName();
  abstract String getDescription();
  abstract String getExample();
  abstract Predicate<Rom> buildPredicate(String token);
  
  protected String[] splitWithDelimiter(String token, String delim)
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
    
    return null;
  }
  
  protected boolean isSearchArg(String[] tokens, String... vals)
  {
    boolean firstMatch = tokens != null && tokens[0].equals(vals[0]);  
    return firstMatch && Arrays.stream(vals, 1, vals.length).anyMatch( v -> v.equals(tokens[1]));
  }
}
