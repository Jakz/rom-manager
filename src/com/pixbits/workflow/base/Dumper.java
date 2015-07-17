package com.pixbits.workflow.base;

import java.util.stream.Stream;
import java.util.function.Consumer;

public abstract class Dumper<T extends Data> implements Consumer<Stream<T>>
{
  abstract public void accept(T item);
  @Override public final void accept(Stream<T> stream) { stream.forEach(i -> accept(i)); }
}
