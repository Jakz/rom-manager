package jack.rm.data.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.searcher.SearchPlugin;
import jack.rm.plugins.searcher.SearchPredicatesPlugin;

public class Searcher
{
  private final SearchParser parser;
  private final List<SearchPredicate> predicates;
  
  protected Searcher()
  {
    parser = null;
    predicates = null;
  }
  
  public Searcher(RomSet romset)
  {
    predicates = new ArrayList<>();
    
    SearchPlugin plugin = romset.getSettings().plugins.getEnabledPlugin(PluginRealType.SEARCH);
    parser = plugin.getSearcher();
    
    Set<SearchPredicatesPlugin> predicates = romset.getSettings().plugins.getEnabledPlugins(PluginRealType.SEARCH_PREDICATES);
    predicates.forEach(p -> this.predicates.addAll(p.getPredicates()));
  }
  
  public Predicate<Rom> search(String text)
  {
    return parser.parse(text).apply(predicates);
  }
}
