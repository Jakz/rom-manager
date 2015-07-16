package jack.rm.json;

import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

class PathAdapter implements JsonSerializer<Path>, JsonDeserializer<Path> {
  @Override
  public JsonElement serialize(Path src, Type type, JsonSerializationContext context)
  {
    return new JsonPrimitive(src.toString());
  }
  
  @Override
  public Path deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    return java.nio.file.Paths.get(json.getAsString());
  }
}