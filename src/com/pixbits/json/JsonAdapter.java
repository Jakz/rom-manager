package com.pixbits.json;

import com.google.gson.*;

public interface JsonAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}
