package jack.rm.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Game;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import jack.rm.data.romset.GameList;;

public class RomListAdapter implements JsonSerializer<GameList>, JsonDeserializer<GameList>
{
  GameList list;
  
  public RomListAdapter(GameList list)
  {
    this.list = list;
  }
    
  @Override
  public JsonElement serialize(GameList src, Type type, JsonSerializationContext context)
  {
    List<RomSavedState> roms = list.stream()
      .filter(Game::shouldSerializeState)
      .map(r -> new RomSavedState(r))
      .collect(Collectors.toList());
    
    return context.serialize(roms, new TypeToken<List<RomSavedState>>(){}.getType());
  }
  
  @Override
  public GameList deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    List<RomSavedState> roms = context.deserialize(json, new TypeToken<List<RomSavedState>>(){}.getType());

    for (RomSavedState prom : roms)
    {
      Game rom = list.getByID(prom.id);
      
      if (rom != null)
      {
        rom.setHandle(prom.file);
        rom.status = prom.status;
        rom.setFavourite(prom.favourite);
        
        if (prom.attributes != null)
          for (RomSavedAttribute attrib : prom.attributes)
            rom.setCustomAttribute(attrib.key, attrib.value);
        
        if (prom.attachments != null)
          rom.getAttachments().addAll(prom.attachments);
        
      }
    }
    
    return list;
  }
}