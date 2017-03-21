package jack.rm.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jack.rm.data.romset.GameSet;
import jack.rm.data.romset.GameSetManager;

class GameSetAdapter implements JsonSerializer<GameSet>, JsonDeserializer<GameSet> {
  @Override
  public JsonElement serialize(GameSet src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.ident());
  }
  
  @Override
  public GameSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	  	return GameSetManager.byIdent(json.getAsJsonPrimitive().getAsString());
	  }
}