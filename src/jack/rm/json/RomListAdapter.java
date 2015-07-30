package jack.rm.json;

import java.util.List;
import java.util.stream.Collectors;
import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomList;;

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
    List<RomSavedState> roms = list.stream()
      .filter(Rom::shouldSerializeState)
      .map(r -> new RomSavedState(r))
      .collect(Collectors.toList());
    
    return context.serialize(roms, new TypeToken<List<RomSavedState>>(){}.getType());
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
        rom.setPath(prom.file);
        rom.status = prom.status;
        rom.setFavourite(prom.favourite);
        
        if (prom.attributes != null)
          for (RomSavedAttribute attrib : prom.attributes)
            rom.setCustomAttribute(attrib.key, attrib.value);
        
      }
    }
    
    return list;
  }
}