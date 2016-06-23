package com.pixbits.functional;

import java.util.function.Predicate;

public class Functional
{
  public static <T,U> Predicate<T> partialApply(Predicate<Pair<T,U>> predicate, U u)
  {
    return t -> predicate.test(new Pair<T,U>(t,u));
  }
  
}
