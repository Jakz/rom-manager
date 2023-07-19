package jack.rm.plugins.types;

import java.util.List;

import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class DataFetcherPlugin extends ActualPlugin
{
  @Override public PluginType<?> getPluginType() { return PluginRealType.DATA_FETCHER; }

  public abstract List<Attribute> supportedAttributes();
}
