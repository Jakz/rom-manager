package jack.rm.data.search;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public class Predicates
{
  private static abstract class BasicPredicate implements SearchPredicate
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
}
