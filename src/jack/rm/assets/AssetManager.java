package jack.rm.assets;

import java.net.URL;
import java.nio.file.Path;

import jack.rm.data.Rom;

public interface AssetManager
{
  public URL assetURL(Asset asset, Rom rom);
  
  public Asset[] getSupportedAssets();
}
