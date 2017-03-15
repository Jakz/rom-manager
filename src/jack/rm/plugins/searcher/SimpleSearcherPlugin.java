package jack.rm.plugins.searcher;

import com.pixbits.lib.searcher.BasicSearchParser;
import com.pixbits.lib.searcher.LambdaPredicate;
import com.pixbits.lib.searcher.SearchParser;
import com.pixbits.lib.searcher.SearchPredicate;
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

  final private SearchPredicate<Rom> freeSearch = new LambdaPredicate<Rom>(token -> r -> r.getTitle().toLowerCase().contains(token));
  
  final private BasicSearchParser<Rom> searcher = new BasicSearchParser<>(freeSearch);
  
  @Override
  public SearchParser<Rom> getSearcher()
  {
    return searcher;
  }

}
