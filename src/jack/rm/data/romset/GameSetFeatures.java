package jack.rm.data.romset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.organizers.GameRenamer;
import com.pixbits.lib.searcher.DummySearcher;
import com.pixbits.lib.searcher.SearchParser;
import com.pixbits.lib.searcher.SearchPredicate;
import com.pixbits.lib.searcher.Searcher;

import jack.rm.Settings;
import jack.rm.files.Scanner;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.searcher.SearchPlugin;
import jack.rm.plugins.searcher.SearchPredicatesPlugin;

public class GameSetFeatures
{
  private final GameSet set;
  private Searcher<Game> searcher;
  private GameRenamer renamer;
  private Scanner scanner;
  
  public GameSetFeatures(GameSet set)
  {
    this.set = set;
    searcher = new DummySearcher<>();
    renamer = GameRenamer.DUMMY;
  }
  
  public void pluginStateChanged()
  {
    Settings settings = set.getSettings();
    
    if (set.getSettings().getSearchPlugin() != null)
    {
      List<SearchPredicate<Game>> predicates = new ArrayList<>();
      
      SearchPlugin plugin = settings.plugins.getEnabledPlugin(PluginRealType.SEARCH);
      SearchParser<Game> parser = plugin.getSearcher();
      
      Set<SearchPredicatesPlugin> predicatePlugins = settings.plugins.getEnabledPlugins(PluginRealType.SEARCH_PREDICATES);
      predicatePlugins.stream().flatMap(p -> p.getPredicates().stream()).forEach(predicates::add);    
      searcher = new Searcher<>(parser, predicates);
    }
    else
      searcher = new DummySearcher<>();
    
    if (settings.getRenamer() != null)
      renamer = settings.getRenamer();
    else
      renamer = GameRenamer.DUMMY;
    
    scanner = new Scanner(set);
  }
  
  public Searcher<Game> searcher() { return searcher; }
  public GameRenamer renamer() { return renamer; }
  public Scanner scanner() { return scanner; }
}
