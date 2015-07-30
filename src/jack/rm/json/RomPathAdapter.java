package jack.rm.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import jack.rm.data.rom.RomPath;
import jack.rm.data.rom.RomPath.Archive;
import jack.rm.data.rom.RomPath.Bin;

public class RomPathAdapter implements JsonDeserializer<RomPath>, JsonSerializer<RomPath>
{
  @Override
  public RomPath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject obj = json.getAsJsonObject();
    
    RomPath.Type type = (RomPath.Type)context.deserialize(obj.get("type"), RomPath.Type.class);
    
    if (type != null)
    {
      if (type == RomPath.Type.BIN)
        return new Bin(java.nio.file.Paths.get((String)context.deserialize(obj.get("file"), String.class)));
      else if (type == RomPath.Type.ZIP)
        return new Archive(java.nio.file.Paths.get((String)context.deserialize(obj.get("file"), String.class)), obj.get("internalName").getAsString());
    }      
    return null;
  }
  
  @Override
  public JsonElement serialize(RomPath entry, Type typeOfT, JsonSerializationContext context)
  {
    JsonObject json = new JsonObject();
    RomPath.Type entryType = entry.type;
    json.add("type", context.serialize(entryType, RomPath.Type.class));
    
    switch (entryType)
    {
      case BIN:
      {
        json.add("file", context.serialize(entry.file().toString(), String.class));
        break;
      }
      case ZIP:
      {
        json.add("file", context.serialize(entry.file().toString(), String.class));
        json.add("internalName", context.serialize(((Archive)entry).internalName, String.class));
        break;
      }
    }
    
    return json;
  }
  
  // serve il serializer
}