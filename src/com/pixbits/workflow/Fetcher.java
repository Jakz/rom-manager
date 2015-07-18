package com.pixbits.workflow;

import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Fetcher<T extends Data> extends AbstractSpliterator<T> implements Supplier<Stream<T>>
{
  public Fetcher(int size)
  {
    super(size, Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
  }
  
  @Override public Stream<T> get()
  {
    return StreamSupport.stream(this, false);
  }
}
