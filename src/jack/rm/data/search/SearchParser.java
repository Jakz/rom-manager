package jack.rm.data.search;

import java.util.function.Predicate;

import jack.rm.data.rom.Rom;

public interface SearchParser
{
  Predicate<Rom> buildPredicate(String string);
}
