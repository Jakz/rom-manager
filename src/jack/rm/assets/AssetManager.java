package jack.rm.assets;

import java.net.URL;

import jack.rm.data.rom.Rom;

public interface AssetManager
{
  public URL assetURL(Asset asset, Rom rom);
  
  public Asset[] getSupportedAssets();
}
