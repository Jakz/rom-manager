package jack.rm.plugins.searcher;

import java.util.function.Function;

import com.pixbits.plugin.PluginType;

import jack.rm.data.rom.Rom;
import jack.rm.data.search.SearchParser;
import jack.rm.data.search.Searcher;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class SearcherPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.SEARCHER; }
  
  public abstract SearchParser getSearcher();
}

