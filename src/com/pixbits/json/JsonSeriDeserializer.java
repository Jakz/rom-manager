package com.pixbits.json;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface JsonSeriDeserializer<T> extends JsonSerializer<T>, JsonDeserializer<T>
{

}
