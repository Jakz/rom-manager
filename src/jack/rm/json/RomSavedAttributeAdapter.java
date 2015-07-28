package jack.rm.json;

import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jack.rm.data.rom.RomAttribute;

class RomSavedAttributeAdapter implements JsonSerializer<RomSavedAttribute>, JsonDeserializer<RomSavedAttribute> {
  @Override
  public JsonElement serialize(RomSavedAttribute src, Type type, JsonSerializationContext context)
  {
    JsonObject object = new JsonObject();
    object.add("key", context.serialize(src.key));
    object.add("value", context.serialize(src.value));
    return object;
  }
  
  @Override
  public RomSavedAttribute deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    RomAttribute attribute = context.deserialize(json.getAsJsonObject().get("key"), RomAttribute.class);
    
    if (attribute.clazz != null)
    {
      Object value = context.deserialize(json.getAsJsonObject().get("value"), attribute.clazz);
      return new RomSavedAttribute(attribute, value);
    }
    
    return null;
  }
}