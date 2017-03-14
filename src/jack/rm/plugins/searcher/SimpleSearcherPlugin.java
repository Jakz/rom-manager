package jack.rm.plugins.searcher;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.pixbits.lib.functional.searcher.BasicSearcher;
import com.pixbits.lib.functional.searcher.DummyPredicate;
import com.pixbits.lib.functional.searcher.SearchParser;
import com.pixbits.lib.functional.searcher.SearchPredicate;
import com.pixbits.lib.parser.SimpleParser;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.data.rom.Rom;

public class SimpleSearcherPlugin extends SearchPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Simple Search Engine", new PluginVersion(1,0), "Jack",
        "This plugins provides basic white space separate free search.");
  }

  final private DummyPredicate<Rom> freeSearch = new DummyPredicate<Rom>()
  {
    @Override
    public Predicate<Rom> buildPredicate(String token)
    {
      return r -> r.getTitle().toLowerCase().contains(token);
    } 
  };
  
  final private BasicSearcher<Rom> searcher = new BasicSearcher<>(freeSearch);
  
  @Override
  public SearchParser<Rom> getSearcher()
  {
    return searcher;
  }

}
