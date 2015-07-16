package jack.rm.json;

import java.util.List;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import jack.rm.data.NumberedRom;
import jack.rm.data.Rom;
import jack.rm.data.RomID;
import jack.rm.data.RomID.CRC;
import jack.rm.data.RomList;
import jack.rm.data.RomStatus;;

public class RomListAdapter implements JsonSerializer<RomList>, JsonDeserializer<RomList>
{
  RomList list;
  
  public RomListAdapter(RomList list)
  {
    this.list = list;
  }
  
  @Override
  public JsonElement serialize(RomList src, Type type, JsonSerializationContext context)
  {
    RomSavedState[] roms = list.stream()
      .filter( r -> r.status != RomStatus.NOT_FOUND)
      .map( r -> new RomSavedState(r.getID(), r.status, r.entry) )
      .toArray( size -> new RomSavedState[size]);
    
    return context.serialize(roms);
  }
  
  @Override
  public RomList deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    List<RomSavedState> roms = context.deserialize(json, new TypeToken<List<RomSavedState>>(){}.getType());

    for (RomSavedState prom : roms)
    {
      Rom rom = list.getByID(prom.id);
      
      if (rom != null)
      {
        rom.entry = prom.file;
        rom.status = prom.status;
      }
    }
    
    return list;
  }
}