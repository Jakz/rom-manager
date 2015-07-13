package jack.rm.json;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import com.google.gson.*;

import jack.rm.plugin.Plugin;
import jack.rm.plugin.PluginSet;

public class JsonPluginSetAdapter implements JsonSerializer<PluginSet>, JsonDeserializer<PluginSet>
{
  @Override
  public JsonElement serialize(PluginSet src, Type type, JsonSerializationContext context)
  {
    JsonArray array = new JsonArray();
    src.stream().forEach( p -> array.add(context.serialize(p)) );
    return array;
  }

  @Override
  public PluginSet deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    JsonArray array = json.getAsJsonArray();
    PluginSet set = new PluginSet();
    array.forEach( a -> set.add(context.deserialize(a, Plugin.class)) );
    return set;
  }
}
