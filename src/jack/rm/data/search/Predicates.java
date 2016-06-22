package jack.rm.data.search;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public class Predicates
{
  private static abstract class BasicPredicate extends SearchPredicate
  {
    final String name;
    final String description;
    final String example;
    
    BasicPredicate(String name, String example, String desc)
    {
      this.name = name; 
      this.description = desc;
      this.example = example;
    }
    
    @Override public abstract Predicate<Rom> buildPredicate(String token);
    @Override public String getName() { return name; }
    @Override public String getExample() { return example; }
    @Override public String getDescription() { return description; }
  }
  
  public final static SearchPredicate HAS_ATTACHMENT = new BasicPredicate("has-attachment", "has:attach", "filters roms with attachments included")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      if (token.startsWith("has:attach"))
        return r -> r.getAttachments().size() != 0;
      else
        return null;
    }
  };
  
  public final static SearchPredicate IS_FAVORITE = new BasicPredicate("is-favorite", "is:favorite, is:fav", "filters roms set as favorite")
  {
    @Override public Predicate<Rom> buildPredicate(String token)
    {
      if (isSearchArg(splitWithDelimiter(token, ":"), "is", "favorite", "favourite", "fav"))
        return r -> r.isFavourite();
      else
        return null;
    }
  };
}
