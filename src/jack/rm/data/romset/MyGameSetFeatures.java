package jack.rm.data.romset;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameSet;
import com.github.jakz.romlib.data.set.GameSetFeatures;
import com.github.jakz.romlib.data.set.organizers.GameRenamer;
import com.pixbits.lib.searcher.DummySearcher;
import com.pixbits.lib.searcher.SearchParser;
import com.pixbits.lib.searcher.SearchPredicate;
import com.pixbits.lib.searcher.Searcher;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.files.Organizer;
import jack.rm.files.Scanner;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.types.SearchPlugin;
import jack.rm.plugins.types.SearchPredicatesPlugin;

public class MyGameSetFeatures implements GameSetFeatures
{
  private final GameSet set;
  private Searcher<Game> searcher;
  private GameRenamer renamer;
  private Scanner scanner;
  private Organizer organizer;
  
  private Set<Feature> features;
    
  public MyGameSetFeatures(GameSet set, Feature... features)
  {
    this.set = set;
    this.searcher = new DummySearcher<>();
    this.renamer = GameRenamer.DUMMY;
    this.features = new HashSet<>(Arrays.asList(features));
    this.organizer = new Organizer(set, this);
  }
  
  public void pluginStateChanged()
  {
    Settings settings = Main.setManager.settings(set);
    
    if (settings.getSearchPlugin() != null)
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
  
  @Override
  public boolean hasFeature(Feature feature)
  {
    return features.contains(feature);
  }

  @Override
  public Searcher<Game> searcher()
  { 
    return searcher;
  }
  
  @Override
  public Path getAttachmentPath()
  {
    return Main.setManager.settings(set).romsPath.resolve(Paths.get("attachments"));
  }

  
  public Organizer organizer() { return organizer; }
  public Settings settings() { return Main.setManager.settings(set); }
  @Override public GameRenamer renamer() { return renamer; }
  public Scanner scanner() { return scanner; }


}
