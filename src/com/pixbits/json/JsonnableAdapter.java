package com.pixbits.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class JsonnableAdapter<T extends Jsonnable> implements JsonSerializer<T>, JsonDeserializer<T>
{

  @Override
  public JsonElement serialize(T value, Type type, JsonSerializationContext context) {
    return value.serialize();
  }

  @Override
  public T deserialize(JsonElement arg0, Type arg1,
      JsonDeserializationContext arg2) throws JsonParseException {
    // TODO Auto-generated method stub
    return null;
  }
  
}
