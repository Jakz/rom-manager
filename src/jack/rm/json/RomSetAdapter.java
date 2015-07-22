package jack.rm.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jack.rm.data.set.RomSet;
import jack.rm.data.set.RomSetManager;

class RomSetAdapter implements JsonSerializer<RomSet>, JsonDeserializer<RomSet> {
  @Override
  public JsonElement serialize(RomSet src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.ident());
  }
  
  @Override
  public RomSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	  	return RomSetManager.byIdent(json.getAsJsonPrimitive().getAsString());
	  }
}