package jack.rm.json;

import java.lang.reflect.Type;

import com.github.jakz.romlib.data.game.RomID;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;;

class RomIdAdapter implements JsonSerializer<RomID<?>>, JsonDeserializer<RomID<?>> {
  @Override
  public JsonElement serialize(RomID<?> src, Type type, JsonSerializationContext context)
  {
    return context.serialize(((RomID.CRC)src).value);
  }
  
  @Override
  public RomID<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    return new RomID.CRC(context.deserialize(json, Long.class));
  }
}