package jack.rm.data.search;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public abstract class BasicPredicate extends SearchPredicate
{
  final String name;
  final String description;
  final String example;
  
  public BasicPredicate(String name, String example, String desc)
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