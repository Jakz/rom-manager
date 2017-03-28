package com.github.jakz.romlib.data.assets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class AssetCache
{
  private Map<Asset, Set<Game>> assetCache;
    
  public AssetCache()
  {
    assetCache = new HashMap<>();
  }
    
  boolean isPresent(Game game, Asset asset)
  {
    Set<Game> games = assetCache.get(asset);
    
    if (games == null)
    {
      fillCache(game.getGameSet(), asset);
      games = assetCache.get(asset);
    }

    return games != null ? games.contains(game) : false;
  }
  
  void rebuild(GameSet set, Asset asset)
  {
    assetCache.clear();
    fillCache(set, asset);
  }
  
  void fillCache(GameSet set, Asset asset)
  {
    try
    {
       Path archiveFile = set.getAssetPath(asset, true);
       
       if (Files.exists(archiveFile))
       {
         ZipFile file = new ZipFile(archiveFile.toFile());
         
         for (Game game : set)
         {
           AssetData data = game.getAssetData(asset);
           Path path = data.getPath();
           
           FileHeader header = file.getFileHeader(path.toString());
           
           if (header != null && (!asset.hasCRC() || header.getCrc32() == data.getCRC()))
           {
             assetCache.computeIfAbsent(asset, k -> new HashSet<Game>()).add(game);
           }
         }
       }
    }
    catch (ZipException e)
    {
      e.printStackTrace();
    }
  }
}
