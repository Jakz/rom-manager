package com.pixbits.workflow;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Mutuator<T extends WorkflowData> extends Function<T,T>
{
  default void setArgument(String key, Object value)
  {
    try
    {
      Field field = this.getClass().getField(key);
      field.setAccessible(true);
      field.set(this, value);
    }
    catch (NoSuchFieldException|IllegalAccessException e)
    {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("unchecked")
  default <K> K getArgument(String key)
  {
    try
    {
      Field field = this.getClass().getField(key);
      field.setAccessible(true);
      return (K)field.get(this);
    }
    catch (NoSuchFieldException|IllegalAccessException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  default Mutuator<T> andThen(Mutuator<T> after) {
    Objects.requireNonNull(after);
    return (T t) -> after.apply(apply(t));
  }
}
