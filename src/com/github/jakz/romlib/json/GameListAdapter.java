package com.github.jakz.romlib.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.set.GameList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.pixbits.lib.functional.StreamUtil;

public class GameListAdapter implements JsonSerializer<GameList>, JsonDeserializer<GameList>
{
  GameList list;
  Map<GameID<?>, Game> gameMap;

  public GameListAdapter(GameList list)
  {
    this.list = list;
  }
  
  private void precomputeGameMap()
  {
    gameMap = list.stream()
      .collect(Collectors.toMap(g -> g.getID(), g -> g, (g1,g2) -> g1, () -> new HashMap<>()));
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
    precomputeGameMap();
    
    List<GameSavedState> roms = context.deserialize(json, new TypeToken<List<GameSavedState>>(){}.getType());

    for (GameSavedState prom : roms)
    {
      Game game = gameMap.get(prom.id);
      
      if (game != null)
      {
        game.setStatus(prom.status);
        game.setFavourite(prom.favourite);
        
        StreamUtil.zip(prom.roms.stream(), game.stream()).forEach( p -> {
          if (p.first.handle != null)
            p.second.setHandle(p.first.handle);
        });
        
        if (prom.attributes != null)
          for (GameSavedAttribute attrib : prom.attributes)
            game.setCustomAttribute(attrib.key, attrib.value);
        
        if (prom.attachments != null)
          game.getAttachments().set(prom.attachments);
        
      }
    }
    
    return list;
  }
}