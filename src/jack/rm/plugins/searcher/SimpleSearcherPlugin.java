package jack.rm.plugins.searcher;


import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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
