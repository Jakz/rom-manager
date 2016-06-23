package jack.rm.plugins.searcher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.rom.Genre;
import jack.rm.data.rom.Location;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomSave;
import jack.rm.data.rom.RomStatus;
import jack.rm.data.search.BasicPredicate;
import jack.rm.data.search.SearchPredicate;

public class BaseSearchPredicates extends SearchPredicatesPlugin
{
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Basic Search Predicates", new PluginVersion(1,0), "Jack",
        "This plugins provides basic search predicates.");
  }
  
  private final static SearchPredicate HAS_ATTACHMENT = new BasicPredicate("has-attachment", "has:attach", "filters roms with attachments included")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      if (token.startsWith("has:attach"))
        return r -> r.getAttachments().size() != 0;
      else
        return null;
    }
  };
  
  private final static SearchPredicate IS_FAVORITE = new BasicPredicate("is-favorite", "is:favorite, is:fav", "filters roms set as favorite")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "favorite", "favourite", "fav"))
        return r -> r.isFavourite();
      else
        return null;
    }
  };
  
  private final static SearchPredicate IS_MISSING = new BasicPredicate("is-missing", "is:missing, is:mis", "filters roms which are missing")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "mis", "missing"))
        return r -> r.status == RomStatus.MISSING;
      else
        return null;
    }
  };
  
  private final static SearchPredicate IS_FOUND = new BasicPredicate("is-found", "is:found", "filters roms which are present")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "found"))
        return r -> r.status != RomStatus.MISSING;
      else
        return null;
    }
  };
  
  private final static SearchPredicate OF_GENRE = new BasicPredicate("genre", "genre:action", "filters games of a specified genre")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null && tokens[0].equals("genre"))
      {
        return r -> {
          Genre genre = r.getAttribute(RomAttribute.GENRE);
          Genre sgenre = Genre.forName(tokens[1]);
          
          return genre != null && sgenre != null && genre == sgenre;
        };
      }
      
      return null;
    }
  };
  
  private final static SearchPredicate OF_SAVE_TYPE = new BasicPredicate("save", "save:\"sram 112\"", "filters games with specified save type")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null && tokens[0].equals("save"))
      {
        String[] itokens = tokens[1].split(" ");
        return r -> {
          RomSave<?> save = r.getAttribute(RomAttribute.SAVE_TYPE);            
          return save != null && Arrays.stream(itokens).allMatch(s -> save.toString().toLowerCase().contains(s.toLowerCase()));   
        };
      }

      return null;
    }
  };
  
  private final static SearchPredicate IS_LOCATION = new BasicPredicate("location", "loc:usa, location:usa", "filters games with specified location")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null)
      {
        if (tokens[0].equals("loc") || tokens[0].equals("location"))
        {
          return r -> {
            Location location = r.getAttribute(RomAttribute.LOCATION);
            return location.fullName.toLowerCase().equals(tokens[1]);
          };
        }
      }
     
      return null;
    }
  };
  
  private final static SearchPredicate HAS_ATTRIBUTE = new BasicPredicate("has-attribute", "has:tag", "filters games with specified attribute present")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      String[] tokens = splitWithDelimiter(token, ":");
      
      if (tokens != null)
      {
        if (tokens[0].equals("has"))
        {
          Optional<RomAttribute> attrib = Arrays.stream(RomAttribute.values()).filter(a -> a.getCaption().toLowerCase().equals(tokens[1])).findFirst();
          
          if (attrib.isPresent())
            return r -> r.getAttribute(attrib.get()) != null;
          else
            return r -> true;
        }
      }
     
      return null;
    }
  };
   
  private final SearchPredicate[] predicates = {
    HAS_ATTACHMENT,
    HAS_ATTRIBUTE,
    IS_FAVORITE,
    IS_FOUND,
    IS_LOCATION,
    IS_MISSING,
    OF_GENRE,
    OF_SAVE_TYPE
  };
  
  @Override
  public List<SearchPredicate> getPredicates()
  {
    return Arrays.asList(predicates);
  }

}
