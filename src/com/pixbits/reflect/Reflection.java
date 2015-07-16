package com.pixbits.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Reflection
{
  public static <T> Type getConcreteTypeOfTypeVariable(Class<?> clazz)
  {
    return getConcreteTypeOfTypeVariable(clazz, 0);
  }
  
  @SuppressWarnings("unchecked")
  public static <T> Type getConcreteTypeOfTypeVariable(Class<?> clazz, int index)
  {
    Type type = ((ParameterizedType)clazz.getGenericSuperclass()).getActualTypeArguments()[index];
    
    if (type instanceof Class)
      return (Class<T>)type;
    else if (type instanceof ParameterizedType)
      return (Class<T>) ((ParameterizedType)type).getRawType();
    else
      return type;
  }
  
  
}
