package jack.rm.assets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomList;
import jack.rm.data.romset.RomSet;
import jack.rm.files.BackgroundOperation;
import jack.rm.files.RomSetWorker;
import net.lingala.zip4j.core.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class AssetPacker
{
  private static void packAssets(RomSet romSet)
  {
    Asset[] assets = romSet.getAssetManager().getSupportedAssets();
    
    Consumer<Boolean> callback = r -> {};
    
    for (int i = assets.length - 1; i >= 0; --i)
    {
      callback = b -> new AssetPackerWorker(romSet, assets[i], callback).execute();
    }
    
    callback.accept(true);
  }
  
  private static class AssetPackerWorker extends RomSetWorker<BackgroundOperation>
  {
    private ZipFile file;
    private ZipParameters params;
    private final Asset asset;
    
    AssetPackerWorker(RomSet romSet, Asset asset, Consumer<Boolean> callback)
    {
      super(romSet,
        new BackgroundOperation()
        {
          public String getTitle() { return "Asset Packer"; }
          public String getProgressText() { return "Packing..."; }
        },
        r -> { AssetData data = r.getAssetData(asset); return data.isPresentAsFile() && !data.isPresentAsArchive(); },
        callback  
      );
      
      this.asset = asset;
      
      try
      {
        Path packPath = romSet.getAssetPath(asset);
        java.io.File packFile = new java.io.File(packPath.toString()+".zip");
        file = new ZipFile(packFile);
      
        params = new ZipParameters();
        params.setCompressionLevel(Zip4jConstants.COMP_STORE);
      }
      catch (ZipException e)
      {
        e.printStackTrace();
      }

    }
    
    public void execute(Rom rom)
    {
      try
      {
        file.addFile(rom.getAssetData(asset).getFinalPath().toFile(), params);
      }
      catch (ZipException e)
      {
        e.printStackTrace();
      }
    }
    
  }
}
