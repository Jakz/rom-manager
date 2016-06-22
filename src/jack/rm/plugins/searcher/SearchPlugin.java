package jack.rm.plugins.searcher;

import com.pixbits.plugin.PluginType;

import jack.rm.data.search.SearchParser;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class SearchPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.SEARCH; }
  
  public abstract SearchParser getSearcher();
}

