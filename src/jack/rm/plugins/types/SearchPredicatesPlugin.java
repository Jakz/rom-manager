package jack.rm.plugins.types;

import java.util.List;

import com.pixbits.lib.searcher.SearchPredicate;
import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class SearchPredicatesPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.SEARCH_PREDICATES; }
  
  public abstract List<SearchPredicate<Game>> getPredicates();
}

