package jack.rm.plugins.searcher;

import com.pixbits.lib.functional.searcher.SearchParser;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.data.rom.Rom;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class SearchPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.SEARCH; }
  
  public abstract SearchParser<Rom> getSearcher();
}

