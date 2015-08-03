package jack.rm.assets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.exception.ZipException;

public class AssetCache
{
  public static AssetCache cache = new AssetCache();

  private Map<Asset, Set<Rom>> assetCache;
    
  AssetCache()
  {
    assetCache = new HashMap<>();
  }
    
  boolean isPresent(Rom rom, Asset asset)
  {
    
    
    Set<Rom> roms = assetCache.get(asset);
    
    if (roms == null)
    {
      fillCache(rom.getRomSet(), asset);
      roms = assetCache.get(asset);
    }
    
    return roms.contains(rom);
  }
  
  void fillCache(RomSet set, Asset asset)
  {
    try
    {
       Path archiveFile = set.getAssetPath(asset, true);
       
       if (Files.exists(archiveFile))
       {
         ZipFile file = new ZipFile(archiveFile.toFile());
         
         for (Rom rom : set.list)
         {
           AssetData data = rom.getAssetData(asset);
           Path path = data.getPath();
           
           FileHeader header = file.getFileHeader(path.toString());
           
           if (header != null && (!asset.hasCRC() || header.getCrc32() == data.getCRC()))
           {
             assetCache.computeIfAbsent(asset, k -> new HashSet<Rom>()).add(rom);
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
