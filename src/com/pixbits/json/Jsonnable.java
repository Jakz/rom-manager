package com.pixbits.json;

import com.google.gson.JsonElement;

public interface Jsonnable<T>
{
  public JsonElement serialize();
  public void unserialize(JsonElement element);
}
