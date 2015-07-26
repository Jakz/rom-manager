package com.pixbits.workflow;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Mutuator<T extends WorkflowData> extends Function<T,T>
{
  default Mutuator<T> andThen(Mutuator<T> after) {
    Objects.requireNonNull(after);
    return (T t) -> after.apply(apply(t));
}
}
