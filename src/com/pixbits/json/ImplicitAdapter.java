package com.pixbits.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public class ImplicitAdapter<T extends Jsonnable<T>> implements JsonAdapter<T>
{
  final Class<T> clazz;

  public ImplicitAdapter(Class<T> clazz) { this.clazz = clazz; }

  public JsonElement serialize(T value, Type type, JsonSerializationContext context)
  {
    return value.serialize();
  }

  public T deserialize(JsonElement element, Type type, JsonDeserializationContext context)
  {
    try {
      T v = clazz.newInstance();
      v.unserialize(element);
      return v;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
