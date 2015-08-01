package jack.rm.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomPath;
import jack.rm.data.rom.RomPath.Archive;
import jack.rm.data.rom.RomPath.Bin;

public class RomAttributeAdapter implements JsonDeserializer<Attribute>, JsonSerializer<Attribute>
{
  @Override
  public Attribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    String value = json.getAsString();
    
    return RomAttribute.valueOf(value);
  }
  
  @Override
  public JsonElement serialize(Attribute entry, Type typeOfT, JsonSerializationContext context)
  {
    if (entry instanceof RomAttribute)
      return context.serialize(((RomAttribute)entry).name(), String.class);
    
    return null;
  }
  
  // serve il serializer
}