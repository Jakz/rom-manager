package jack.rm.plugins.searcher;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.pixbits.functional.Pair;
import com.pixbits.parser.SimpleParser;
import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.rom.Rom;
import jack.rm.data.search.SearchParser;
import jack.rm.data.search.SearchPredicate;

public class SimpleSearcherPlugin extends SearchPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Simple Search Engine", new PluginVersion(1,0), "Jack",
        "This plugins provides basic white space separate free search.");
  }
  
  private class SimpleSearcher extends SearchParser
  {
    final private SimpleParser parser;
    
    SimpleSearcher()
    {
      parser = new SimpleParser();
      parser.addWhiteSpace(' ');
      parser.addQuote('\"');
    }
    
    @Override
    public Function<List<SearchPredicate>, Predicate<Rom>> parse(String text)
    {
      Function<List<SearchPredicate>, Predicate<Rom>> lambda = predicates -> {
        Predicate<Rom> predicate = p -> true;
        
        List<String> tokens = new ArrayList<>();
        Consumer<String> callback = s -> tokens.add(s); 
      
        parser.setCallback(callback);
        parser.reset(new java.io.ByteArrayInputStream(text.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        
        try {
          parser.parse();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        
        for (String tk : tokens)
        {
          final String token;
          boolean negated = false;
          
          if (tk.startsWith("!"))
          {
            negated = true;
            token = tk.substring(1);
          }
          else
            token = tk;
          
          Predicate<Rom> cpredicate = buildSinglePredicate(predicates, token);
          
          if (cpredicate != null)
          {
            if (negated)
              predicate = predicate.and(cpredicate.negate());
            else
              predicate = predicate.and(cpredicate);
          }
          else
          {
            Predicate<Rom> freeSearch = r -> r.getTitle().toLowerCase().contains(token);
            
            if (negated) predicate = predicate.and(freeSearch.negate());
            else predicate = predicate.and(freeSearch);
          }

        }
      
        return predicate;
      };
      
      return lambda;
    }
    
  }
  
  final private SimpleSearcher searcher = new SimpleSearcher();
  
  @Override
  public SearchParser getSearcher()
  {
    return searcher;
  }

}
