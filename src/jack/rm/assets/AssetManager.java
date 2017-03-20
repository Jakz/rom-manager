package jack.rm.assets;

import java.net.URL;

import com.github.jakz.romlib.data.game.Game;

public interface AssetManager
{
  public URL assetURL(Asset asset, Game rom);
  
  public Asset[] getSupportedAssets();
}
