package jack.rm.plugins.searcher;

import java.util.List;

import com.pixbits.plugin.PluginType;

import jack.rm.data.search.SearchPredicate;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class SearchPredicatesPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.SEARCH_PREDICATES; }
  
  public abstract List<SearchPredicate> getPredicates();
}

