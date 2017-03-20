package jack.rm.assets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.jakz.romlib.data.game.Game;

import jack.rm.data.romset.GameSet;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

public class AssetCache
{
  public static AssetCache cache = new AssetCache();

  private Map<Asset, Set<Game>> assetCache;
    
  AssetCache()
  {
    assetCache = new HashMap<>();
  }
    
  boolean isPresent(Game rom, Asset asset)
  {
    Set<Game> roms = assetCache.get(asset);
    
    if (roms == null)
    {
      fillCache(rom.getRomSet(), asset);
      roms = assetCache.get(asset);
    }

    return roms != null ? roms.contains(rom) : false;
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
         
         for (Game rom : set.list)
         {
           AssetData data = rom.getAssetData(asset);
           Path path = data.getPath();
           
           FileHeader header = file.getFileHeader(path.toString());
           
           if (header != null && (!asset.hasCRC() || header.getCrc32() == data.getCRC()))
           {
             assetCache.computeIfAbsent(asset, k -> new HashSet<Game>()).add(rom);
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
