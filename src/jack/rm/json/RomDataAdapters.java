package jack.rm.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jack.rm.data.rom.Version;

public class RomDataAdapters
{
  class VersionAdapter implements JsonSerializer<Version>, JsonDeserializer<Version>
  {

    @Override
    public Version deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
      if (json == null)
        return new Version.Unspecified();
      
      if (json.isJsonPrimitive())
      {
        JsonPrimitive primitive = json.getAsJsonPrimitive();
        
        //if (primitive.isString() && primitive.getAsString().equals("UNSPECIFIED")))
      }
      
      return null;
    }

    @Override
    public JsonElement serialize(Version json, Type type, JsonSerializationContext context)
    {
      return null;
    }
    
  }
}
