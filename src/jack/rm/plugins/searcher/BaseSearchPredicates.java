package jack.rm.plugins.searcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.pixbits.lib.searcher.BasicPredicate;
import com.pixbits.lib.searcher.SearchPredicate;

import jack.rm.plugins.types.SearchPredicatesPlugin;

import com.github.jakz.romlib.data.game.Genre;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.LocationSet;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameSave;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

public class BaseSearchPredicates extends SearchPredicatesPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Basic Search Predicates", new PluginVersion(1,0), "Jack",
        "This plugins provides basic search predicates.");
  }
  
  private final static SearchPredicate<Game> IS_MULTIPLE_ROM = new BasicPredicate<Game>("is-multiple", "is:multiple", "filters games with multiple roms")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (token.startsWith("is:multiple"))
        return r -> r.romCount() > 1;
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> HAS_ATTACHMENT = new BasicPredicate<Game>("has-attachment", "has:attach", "filters games with attachments included")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (token.startsWith("has:attach"))
        return r -> r.getAttachments().size() != 0;
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> HAS_ASSETS = new BasicPredicate<Game>("has-assets", "has:assets", "filters games with assets downloaded")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (token.equals("has:assets"))
        return r -> r.hasAllAssets();
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_FAVORITE = new BasicPredicate<Game>("is-favorite", "is:favorite, is:fav", "filters games set as favorite")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "favorite", "favourite", "fav"))
        return r -> r.isFavourite();
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_MISSING = new BasicPredicate<Game>("is-missing", "is:missing, is:mis", "filters games which are missing")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "mis", "missing"))
        return r -> !r.getStatus().isComplete();
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_FOUND = new BasicPredicate<Game>("is-found", "is:found", "filters games which are present")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "found"))
        return r -> r.getStatus().isComplete();
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_BAD_DUMP = new BasicPredicate<Game>("is-bad", "is:bad", "filters games which known bad dumps")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "bad"))
        return g -> g.getBoolAttribute(GameAttribute.BAD_DUMP);
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> OF_GENRE = new BasicPredicate<Game>("genre", "genre:action", "filters games of a specified genre")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null && tokens[0].equals("genre"))
      {
        return r -> {
          Genre genre = r.getAttribute(GameAttribute.GENRE);
          Genre sgenre = Genre.forName(tokens[1]);
          
          return genre != null && sgenre != null && genre == sgenre;
        };
      }
      
      return null;
    }
  };
  
  private final static SearchPredicate<Game> OF_SAVE_TYPE = new BasicPredicate<Game>("save", "save:\"sram 112\"", "filters games with specified save type")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null && tokens[0].equals("save"))
      {
        String[] itokens = tokens[1].split(" ");
        return r -> {
          GameSave<?> save = r.getAttribute(GameAttribute.SAVE_TYPE);            
          return save != null && Arrays.stream(itokens).allMatch(s -> save.toString().toLowerCase().contains(s.toLowerCase()));   
        };
      }

      return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_LOCATION = new BasicPredicate<Game>("location", "loc:usa, location:usa", "filters games with specified location")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null)
      {
        if (tokens[0].equals("loc") || tokens[0].equals("location"))
        {
          return r -> {
            LocationSet location = r.getLocation();
            Location slocation = Location.forName(tokens[1]);
            return slocation == null || location.is(slocation);
          };
        }
      }
     
      return null;
    }
  };
  
  private final static SearchPredicate<Game> SIZE_SPEC = new BasicPredicate<Game>("size", "size>10mb", "filters games according to size")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      return null;

    }
  };
  
  private final static SearchPredicate<Game> HAS_ATTRIBUTE = new BasicPredicate<Game>("has-attribute", "has:tag", "filters games with specified attribute present")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null)
      {
        if (tokens[0].equals("has"))
        {
          Optional<GameAttribute> attrib = Arrays.stream(GameAttribute.values()).filter(a -> a.getCaption().toLowerCase().equals(tokens[1])).findFirst();
          
          if (attrib.isPresent())
            return r -> r.getAttribute(attrib.get()) != null;
          else
            return r -> true;
        }
      }
     
      return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_FORMAT = new BasicPredicate<Game>("is-format", "format:zip", "filters games by format on disk")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null)
      {
        if (tokens[0].equals("format") && !tokens[1].isEmpty())
        {
          final Predicate<Rom> typePredicate = tokens[1].equals("bin") || tokens[1].equals("binary") ? 
             r -> !r.handle().isArchive() : r -> r.handle().getExtension().equals(tokens[1]);

          final Predicate<Game> hasAtleastOneRomFound = g -> g.stream().anyMatch(Rom::isPresent);
          
          return hasAtleastOneRomFound.and(g -> g.stream().filter(Rom::isPresent).allMatch(typePredicate));
        }
      }
     
      return null;
    }
  };
   
  private final List<SearchPredicate<Game>> predicates;
  
  public BaseSearchPredicates()
  {
    predicates = new ArrayList<>();
    predicates.add(IS_MULTIPLE_ROM);
    predicates.add(HAS_ATTACHMENT);
    predicates.add(HAS_ASSETS);
    predicates.add(HAS_ATTRIBUTE);
    predicates.add(IS_FAVORITE);
    predicates.add(IS_FOUND);
    predicates.add(IS_LOCATION);
    predicates.add(IS_MISSING);
    predicates.add(OF_GENRE);
    predicates.add(OF_SAVE_TYPE);
    predicates.add(IS_FORMAT);
    predicates.add(IS_BAD_DUMP);

  }

  @Override
  public List<SearchPredicate<Game>> getPredicates()
  {
    return predicates;
  }

}
