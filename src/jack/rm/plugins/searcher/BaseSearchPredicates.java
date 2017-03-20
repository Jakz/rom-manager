package jack.rm.plugins.searcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.pixbits.lib.searcher.BasicPredicate;
import com.pixbits.lib.searcher.SearchPredicate;
import com.github.jakz.romlib.data.game.Genre;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameSave;
import com.github.jakz.romlib.data.game.GameStatus;
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
  
  private final static SearchPredicate<Game> HAS_ATTACHMENT = new BasicPredicate<Game>("has-attachment", "has:attach", "filters roms with attachments included")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (token.startsWith("has:attach"))
        return r -> r.getAttachments().size() != 0;
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> HAS_ASSETS = new BasicPredicate<Game>("has-assets", "has:assets", "filters roms with assets downloaded")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (token.equals("has:assets"))
        return r -> r.hasAllAssets();
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_FAVORITE = new BasicPredicate<Game>("is-favorite", "is:favorite, is:fav", "filters roms set as favorite")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "favorite", "favourite", "fav"))
        return r -> r.isFavourite();
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_MISSING = new BasicPredicate<Game>("is-missing", "is:missing, is:mis", "filters roms which are missing")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "mis", "missing"))
        return r -> r.status == GameStatus.MISSING;
      else
        return null;
    }
  };
  
  private final static SearchPredicate<Game> IS_FOUND = new BasicPredicate<Game>("is-found", "is:found", "filters roms which are present")
  {
    @Override public Predicate<Game> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "found"))
        return r -> r.status != GameStatus.MISSING;
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
            Location location = r.getAttribute(GameAttribute.LOCATION);
            return location.fullName.toLowerCase().equals(tokens[1]);
          };
        }
      }
     
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
          if (tokens[1].equals("bin") || tokens[1].equals("binary"))
            return r -> r.status != GameStatus.MISSING && !r.getHandle().isArchive();
          else 
            return r -> r.status != GameStatus.MISSING && r.getHandle().isArchive() && r.getHandle().getExtension().equals(tokens[1]);
        }
      }
     
      return null;
    }
  };
   
  private final List<SearchPredicate<Game>> predicates;
  
  public BaseSearchPredicates()
  {
    predicates = new ArrayList<>();
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

  }

  @Override
  public List<SearchPredicate<Game>> getPredicates()
  {
    return predicates;
  }

}
