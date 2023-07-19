package jack.rm.plugins.fetchers;

import java.net.URL;
import java.util.List;

import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.plugins.types.DataFetcherPlugin;

public class MobyGamesFetcher extends DataFetcherPlugin
{
  private final String API_KEY = "moby_HHsziFeryZRVjgX8Yvjku92zuaz";
  private final String url = "https://api.mobygames.com/v1/games?api_key=" + API_KEY;
  
  // https://api.mobygames.com/v1/games?title=mario%20advance&platform=12&format=brief
  
  public MobyGamesFetcher()
  {

  }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("MobyGames Data Fetcher", new PluginVersion(1,0), "Jack",
        "This plugin gathers game data from MobyGames API.");
  }
  
  @Override
  public List<Attribute> supportedAttributes()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
