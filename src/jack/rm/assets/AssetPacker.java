package jack.rm.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jack.rm.data.Rom;
import jack.rm.data.RomList;
import jack.rm.data.set.RomSet;

import net.lingala.zip4j.core.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class AssetPacker
{
  public static void packAssets(RomSet romSet)
  {
    Asset[] assets = romSet.getAssetManager().getSupportedAssets();
    
    try
    {
      for (Asset asset : assets)
        packAsset(asset, romSet);
    }
    catch (ZipException|IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private static void packAsset(Asset asset, RomSet romSet) throws ZipException, IOException
  {
    RomList list = romSet.list;
    
    Path packPath = romSet.getAssetPath(asset, null);
    
    java.io.File packFile = new java.io.File(packPath.toString()+".zip");
    if (Files.exists(packFile.toPath()))
      Files.delete(packFile.toPath());
    ZipFile file = new ZipFile(packFile);
    
    ZipParameters params = new ZipParameters();
    params.setCompressionLevel(Zip4jConstants.COMP_STORE);

    for (Rom rom : list)
    {
      if (rom.hasAsset(asset))
      {
        file.addFile(romSet.getAssetPath(asset, rom).toFile(), params);
      }
    }
  }
}
