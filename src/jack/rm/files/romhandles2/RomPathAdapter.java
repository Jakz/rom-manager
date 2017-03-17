package jack.rm.files.romhandles2;

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

public class RomPathAdapter implements JsonDeserializer<RomHandle>, JsonSerializer<RomHandle>
{
  @Override
  public RomHandle deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    JsonObject obj = json.getAsJsonObject();
    
    RomHandle.Type type = (RomHandle.Type)context.deserialize(obj.get("type"), RomHandle.Type.class);
    
    Path file = Paths.get((String)context.deserialize(obj.get("file"), String.class));
    
    if (type != null)
    {
      if (type == RomHandle.Type.BIN)
        return new BinaryHandle(file);
      else if (type == RomHandle.Type.ZIP)
        return new ZipHandle(file, obj.get("internalName").getAsString());
      else if (type == RomHandle.Type._7ZIP)
        return new Zip7MultiHandle(type, file, obj.get("internalName").getAsString(), obj.get("indexInArchive").getAsInt());
      else if (type == RomHandle.Type.RAR)
        return new Zip7MultiHandle(type, file, obj.get("internalName").getAsString(), obj.get("indexInArchive").getAsInt());
    }      
    return null;
  }
  
  @Override
  public JsonElement serialize(RomHandle entry, Type typeOfT, JsonSerializationContext context)
  {
    JsonObject json = new JsonObject();
    RomHandle.Type entryType = entry.type;
    json.add("type", context.serialize(entryType, RomHandle.Type.class));
    
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
      case RAR:
      case _7ZIP:
      {
        json.add("file", context.serialize(entry.file().toString(), String.class));
        json.add("internalName", context.serialize(((Zip7MultiHandle)entry).internalName, String.class));
        json.add("indexInArchive", context.serialize(((Zip7MultiHandle)entry).indexInArchive, Integer.class));
        break;
      }
    }
    
    return json;
  }
  
  // serve il serializer
}