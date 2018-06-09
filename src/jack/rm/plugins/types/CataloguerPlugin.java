package jack.rm.plugins.types;

import java.util.Optional;

import com.github.jakz.romlib.data.cataloguers.CloneSetCreator;
import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.cataloguers.GameListTransformer;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class CataloguerPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.CATALOGUER; }

  abstract public Optional<GameCataloguer> getCataloguer(); 
  abstract public Optional<GameListTransformer> getGameListTransformer();
  abstract public Optional<CloneSetCreator> getCloneSetCreator();
  
}
