package jack.rm.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.concurrent.OperationDetails;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class AssetPacker
{
  public static void packAssets(GameSet gameSet)
  {
    final Asset[] assets = gameSet.getAssetManager().getSupportedAssets();
    
    Consumer<Boolean> callback = r -> {
      Arrays.stream(assets).forEach(asset -> gameSet.assetCache().rebuild(gameSet, asset));
    };
    
    for (int i = assets.length - 1; i >= 0; --i)
    {
      BiFunction<Integer, Consumer<Boolean>, Consumer<Boolean>> a = (c, cb) -> b -> new AssetPackerWorker(gameSet, assets[c], cb).execute();
      callback = a.apply(i, callback);
    }
    
    callback.accept(true);
  }
  
  private static class AssetPackerWorker extends RomSetWorker<OperationDetails>
  {
    private ZipFile file;
    private ZipParameters params;
    private final Asset asset;
    
    AssetPackerWorker(GameSet romSet, Asset asset, Consumer<Boolean> callback)
    {
      super(romSet,
        new OperationDetails()
        {
          public String getTitle() { return "Asset Packer"; }
          public String getProgressText() { return "Packing..."; }
        },
        r -> { AssetData data = r.getAssetData(asset); return data.isPresentAsFile() /*&& !data.isPresentAsArchive()*/; },
        callback  
      );
      
      this.asset = asset;
      
      try
      {
        java.io.File packFile = romSet.getAssetPath(asset, true).toFile();
        file = new ZipFile(packFile);
      
        params = new ZipParameters();
        params.setCompressionLevel(Zip4jConstants.COMP_STORE);
      }
      catch (ZipException e)
      {
        e.printStackTrace();
      }

    }
    
    public void execute(Game rom)
    {
      try
      {
        AssetData data = rom.getAssetData(asset);
        
        if (data.isPresentAsFile())
        {
          Path assetPath = data.getFinalPath();
        
          file.addFile(assetPath.toFile(), params);
          Files.delete(assetPath);
        }
      }
      catch (ZipException|IOException e)
      {
        e.printStackTrace();
      }
    }
    
  }
}
