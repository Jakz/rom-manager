package jack.rm.data.set;

import jack.rm.data.Asset;
import jack.rm.data.console.System;
import jack.rm.net.AssetManager;

public interface Provider
{
  public String getTag();
  public String getName();
  
  public AssetManager getAssetManager();
  public Asset[] getSupportedAssets();
  
  public RomSet<?> buildRomSet(System system);
  public System[] getSupportedSystems(System system);
}
