package jack.rm.assets;

import java.net.URL;

import jack.rm.data.rom.Rom;

public class EmptyAssetManager implements AssetManager
{
  public URL assetURL(Asset asset, Rom rom) { return null; }
  
  public Asset[] getSupportedAssets() { return new Asset[0]; }
}
