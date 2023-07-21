package jack.rm.plugins.fetchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.plugins.types.DataFetcherPlugin;

public class MobyGamesFetcher extends DataFetcherPlugin
{
  private final String API_KEY = "moby_HHsziFeryZRVjgX8Yvjku92zuaz";
  private final String url = "https://api.mobygames.com/v1/games?api_key=" + API_KEY;
  
  static Map<Platform, Integer> platformMapping = Map.of(
      Platforms.AMIGA,     19,
      Platforms.GB,        10
  );
  
  // https://api.mobygames.com/v1/games?title=mario%20advance&platform=12&format=brief
  
  // https://api.mobygames.com/v1/games?format=id&title=onslaught&platform=19
  
  String httpRequestToString(String urls, String... args)
  {
    String completeURL = urls;
    
    for (int i = 0; i < args.length / 2; ++i)
    {
      completeURL += (i == 0) ? "?" : "&";
      completeURL += args[2 * i] + "=" + args[2 * i + 1];
    }
    
    try
    {
      this.debug("json request: " + completeURL);
      
      URL url = new URL(completeURL);
      
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");

      if (conn.getResponseCode() != 200)
        throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

      BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
      StringBuilder output = new StringBuilder();

      String line;
      System.out.println("Output from Server .... \n");
      while ((line = br.readLine()) != null) {
        output.append(line + "\n");
      }
      
      return output.toString();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  JsonElement httpRequestToJson(String urls, String... args)
  {
    String response = httpRequestToString(urls, args);
    JsonParser parser = new JsonParser();
    JsonElement element = parser.parse(response);
    return element;
  }
  
  /* fetch platforms from MobyGames API */
  void loadPlatforms()
  {
    if (true)
      return;
    
    var json = httpRequestToJson("https://api.mobygames.com/v1/platforms", "api_key", API_KEY);
    
    Gson gson = new Gson();
    List<MobyGames.Platform> platforms = gson.fromJson(json.getAsJsonObject().get("platforms").toString(), new TypeToken<List<MobyGames.Platform>>(){}.getType());
    
    message("parsed "+platforms.size()+" platforms");
    
    Set<String> set1 = new HashSet<>();
    Set<String> set2 = new HashSet<>();
    
    /* try to match platform ids to actual systems */
    for (Platform platform : Platforms.values())
    {
      boolean found = false;
      set1.clear();
      String[] tokens = platform.getName().toLowerCase().split(" ");
      //set1.addAll(tokens);

      for (MobyGames.Platform mplatform : platforms)
      {
        if (platform.getName().toLowerCase().equals(mplatform.platform_name.toLowerCase()))
        {
          message("Found match for " + platform.getName());
          found = true;
          break;
        }
      }
      
      if (!found)
        warning("Didn't found a match for " + platform.getName());
    }
    
  }
  
  
  
  public MobyGamesFetcher()
  {
    loadPlatforms();
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
