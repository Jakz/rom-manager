package jack.rm.assets;

import java.net.URL;

import com.github.jakz.romlib.data.game.Game;

public interface AssetManager
{
  URL assetURL(Asset asset, Game rom);
  Asset[] getSupportedAssets();
  default boolean hasAssets() { return getSupportedAssets().length > 0; }
  
  public static final AssetManager DUMMY = new AssetManager()
  {
    @Override public URL assetURL(Asset asset, Game rom) { return null; }
    @Override public Asset[] getSupportedAssets() { return new Asset[0]; }
  };
}
