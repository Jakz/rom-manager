package jack.rm.json;

import java.lang.reflect.Type;

import com.github.jakz.romlib.data.game.GameID;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;;

class GameIdAdapter implements JsonSerializer<GameID<?>>, JsonDeserializer<GameID<?>> {
  @Override
  public JsonElement serialize(GameID<?> src, Type type, JsonSerializationContext context)
  {
    return context.serialize(((GameID.CRC)src).value);
  }
  
  @Override
  public GameID<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    return new GameID.CRC(context.deserialize(json, Long.class));
  }
}