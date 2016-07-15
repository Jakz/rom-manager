package com.pixbits.workflow;

import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class Dumper<T extends WorkflowData> implements Consumer<Stream<T>>
{
  abstract public void accept(T item);
  @Override public final void accept(Stream<T> stream) { stream.forEach(i -> accept(i)); }
}
