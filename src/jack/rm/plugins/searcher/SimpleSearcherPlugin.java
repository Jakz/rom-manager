package jack.rm.plugins.searcher;

import com.pixbits.lib.searcher.BasicSearchParser;
import com.pixbits.lib.searcher.LambdaPredicate;
import com.pixbits.lib.searcher.SearchParser;
import com.pixbits.lib.searcher.SearchPredicate;

import jack.rm.plugins.types.SearchPlugin;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

public class SimpleSearcherPlugin extends SearchPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Simple Search Engine", new PluginVersion(1,0), "Jack",
        "This plugins provides basic white space separate free search.");
  }

  final private SearchPredicate<Game> freeSearch = new LambdaPredicate<Game>(token -> r -> r.getTitle().toLowerCase().contains(token));
  
  final private BasicSearchParser<Game> searcher = new BasicSearchParser<>(freeSearch);
  
  @Override
  public SearchParser<Game> getSearcher()
  {
    return searcher;
  }

}
