package jack.rm.data.search;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public interface SearchPredicate
{
  String getName();
  String getDescription();
  String getExample();
  Predicate<Rom> buildPredicate(String token);
}
