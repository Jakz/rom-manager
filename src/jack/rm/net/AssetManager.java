package jack.rm.net;

import java.net.URL;
import java.nio.file.Path;

import jack.rm.data.Asset;
import jack.rm.data.Rom;

public interface AssetManager
{
  public URL assetURL(Asset asset, Rom rom);
  public Path assetPath(Asset asset, Rom rom);
  
  public Asset[] getSupportedAssets();
}
