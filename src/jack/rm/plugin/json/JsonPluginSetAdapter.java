package jack.rm.plugin.json;

import java.lang.reflect.Type;
import com.google.gson.*;
import com.pixbits.plugin.Plugin;
import com.pixbits.plugin.PluginSet;

public class JsonPluginSetAdapter<T extends Plugin> implements JsonSerializer<PluginSet<T>>, JsonDeserializer<PluginSet<T>>
{
  @Override
  public JsonElement serialize(PluginSet<T> src, Type type, JsonSerializationContext context)
  {
    JsonArray array = new JsonArray();
    src.stream().forEach( p -> array.add(context.serialize(p)) );
    return array;
  }

  @Override
  public PluginSet<T> deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    JsonArray array = json.getAsJsonArray();
    PluginSet<T> set = new PluginSet<T>();
    array.forEach( a -> set.add(context.deserialize(a, Plugin.class)) );
    return set;
  }
}
