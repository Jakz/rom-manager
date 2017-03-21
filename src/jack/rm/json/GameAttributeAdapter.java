package jack.rm.json;

import java.lang.reflect.Type;

import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GameAttributeAdapter implements JsonDeserializer<Attribute>, JsonSerializer<Attribute>
{
  @Override
  public Attribute deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    String value = json.getAsString();
    
    return GameAttribute.valueOf(value);
  }
  
  @Override
  public JsonElement serialize(Attribute entry, Type typeOfT, JsonSerializationContext context)
  {
    if (entry instanceof GameAttribute)
      return context.serialize(((GameAttribute)entry).name(), String.class);
    
    return null;
  }
  
  // serve il serializer
}