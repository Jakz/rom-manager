package jack.rm.plugins.types;

import com.pixbits.lib.searcher.SearchParser;
import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class SearchPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.SEARCH; }
  
  public abstract SearchParser<Game> getSearcher();
}

