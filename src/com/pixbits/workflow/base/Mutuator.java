package com.pixbits.workflow.base;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Mutuator<T extends Data> extends Function<T,T>
{
  default Mutuator<T> andThen(Mutuator<T> after) {
    Objects.requireNonNull(after);
    return (T t) -> after.apply(apply(t));
}
}
