package com.pixbits.json;

import com.google.gson.JsonElement;

public interface Jsonnable
{
  JsonElement serialize();
  void unserialize(JsonElement element);
}
