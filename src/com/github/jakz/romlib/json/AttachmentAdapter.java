package com.github.jakz.romlib.json;

import java.lang.reflect.Type;
import java.nio.file.Path;

import com.github.jakz.romlib.data.attachments.Attachment;
import com.github.jakz.romlib.data.attachments.AttachmentType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AttachmentAdapter implements JsonDeserializer<Attachment>, JsonSerializer<Attachment>
{
  @Override
  public Attachment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject o = json.getAsJsonObject();
    
    Attachment attach = new Attachment();
    attach.setPath(context.deserialize(o.get("path"), Path.class));
    attach.setName(context.deserialize(o.get("name"), String.class));
    attach.setDescription(context.deserialize(o.get("description"), String.class));
    attach.setType(context.deserialize(o.get("type"), AttachmentType.class));
    
    if (o.has("subtype"))
    {
      attach.setSubType(context.deserialize(o.get("subtype"), attach.getType().getSubTypes()[0].getClass()));
    }
    
    return attach;
  }
  
  @Override
  public JsonElement serialize(Attachment entry, Type typeOfT, JsonSerializationContext context)
  {
    JsonObject json = new JsonObject();

    json.add("path", context.serialize(entry.getPath()));
    json.add("name", context.serialize(entry.getName()));
    json.add("description", context.serialize(entry.getDescription()));
    json.add("type", context.serialize(entry.getType()));
    
    if (entry.getSubType() != null)
      json.add("subtype", context.serialize(entry.getSubType(), entry.getType().getSubTypes()[0].getClass()));
    
    return json;
  }
}