package jack.rm.assets;

import java.net.URL;

import com.github.jakz.romlib.data.game.Game;

public class DummyAssetManager implements AssetManager
{
  public URL assetURL(Asset asset, Game rom) { return null; }
  
  public Asset[] getSupportedAssets() { return new Asset[0]; }
}
