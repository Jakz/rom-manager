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

public class GameListAdapter implements JsonSerializer<GameList>, JsonDeserializer<GameList>
{
  GameList list;
  
  public GameListAdapter(GameList list)
  {
    this.list = list;
  }
    
  @Override
  public JsonElement serialize(GameList src, Type type, JsonSerializationContext context)
  {
    List<GameSavedState> roms = list.stream()
      .filter(Game::shouldSerializeState)
      .map(r -> new GameSavedState(r))
      .collect(Collectors.toList());
    
    return context.serialize(roms, new TypeToken<List<GameSavedState>>(){}.getType());
  }
  
  @Override
  public GameList deserialize(JsonElement json, Type type, JsonDeserializationContext context)
  {
    List<GameSavedState> roms = context.deserialize(json, new TypeToken<List<GameSavedState>>(){}.getType());

    for (GameSavedState prom : roms)
    {
      Game rom = list.getByID(prom.id);
      
      if (rom != null)
      {
        rom.setHandle(prom.file);
        rom.status = prom.status;
        rom.setFavourite(prom.favourite);
        
        if (prom.attributes != null)
          for (GameSavedAttribute attrib : prom.attributes)
            rom.setCustomAttribute(attrib.key, attrib.value);
        
        if (prom.attachments != null)
          rom.getAttachments().set(prom.attachments);
        
      }
    }
    
    return list;
  }
}