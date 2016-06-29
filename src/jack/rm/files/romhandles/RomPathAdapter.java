package jack.rm.files.romhandles;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RomPathAdapter implements JsonDeserializer<RomPath>, JsonSerializer<RomPath>
{
  @Override
  public RomPath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject obj = json.getAsJsonObject();
    
    RomPath.Type type = (RomPath.Type)context.deserialize(obj.get("type"), RomPath.Type.class);
    
    Path file = Paths.get((String)context.deserialize(obj.get("file"), String.class));
    
    if (type != null)
    {
      if (type == RomPath.Type.BIN)
        return new BinaryHandle(file);
      else if (type == RomPath.Type.ZIP)
        return new ZipHandle(file, obj.get("internalName").getAsString());
      else if (type == RomPath.Type._7ZIP)
        return new Zip7Handle(file, obj.get("internalName").getAsString(), obj.get("indexInArchive").getAsInt());
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
        json.add("internalName", context.serialize(((ZipHandle)entry).internalName, String.class));
        break;
      }
      case _7ZIP:
      {
        json.add("file", context.serialize(entry.file().toString(), String.class));
        json.add("internalName", context.serialize(((Zip7Handle)entry).internalName, String.class));
        json.add("indexInArchive", context.serialize(((Zip7Handle)entry).indexInArchive, Integer.class));

      }
    }
    
    return json;
  }
  
  // serve il serializer
}