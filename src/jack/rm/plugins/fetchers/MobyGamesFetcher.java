package jack.rm.plugins.fetchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.digest.DigestOptions;
import com.pixbits.lib.io.digest.Digester;
import com.pixbits.lib.lang.StringUtils;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.plugins.fetchers.MobyGames.Sample;
import jack.rm.plugins.types.DataFetcherPlugin;

public class MobyGamesFetcher extends DataFetcherPlugin
{
  private final String API_KEY = "moby_HHsziFeryZRVjgX8Yvjku92zuaz";
  private final String url = "https://api.mobygames.com/v1/games?api_key=" + API_KEY;

  static Map<Platform, Integer> platformMapping = Map.of(Platforms.AMIGA, 19, Platforms.GB, 10, Platforms.IBM_PC, 2,
      Platforms.NES, 22, Platforms.PSP, 46
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
      while ((line = br.readLine()) != null)
      {
        output.append(line + "\n");
      }

      return output.toString();
    } catch (IOException e)
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

  private String buildTitleQuery(Game game)
  {
    String title = game.getNormalizedTitle();
    StringBuilder query = new StringBuilder();

    for (int i = 0; i < title.length(); ++i)
    {
      char c = title.charAt(i);

      if (Character.isAlphabetic(c))
        query.append(c);
      else
        query.append(' ');
    }

    return query.toString();
  }

  private int findGameID(Game game)
  {
    /* generate url */
    var data = httpRequestToJson("https://api.mobygames.com/v1/games", "api_key", API_KEY, "platform",
        Integer.toString(platformMapping.get(game.getPlatform())), "format", "brief", "title", buildTitleQuery(game));

    Gson gson = new Gson();
    JsonObject root = gson.fromJson(data, JsonObject.class);
    MobyGames.Games ids = gson.fromJson(root, MobyGames.Games.class);
    
    message(" found " + ids.games.size() + " results");

    return ids.games.size() == 1 ? ids.games.get(0).game_id : -1;
  }

  /* fetch platforms from MobyGames API */
  void loadPlatforms()
  {
    if (true)
      return;

    var json = httpRequestToJson("https://api.mobygames.com/v1/platforms", "api_key", API_KEY);

    Gson gson = new Gson();
    List<MobyGames.Platform> platforms = gson.fromJson(json.getAsJsonObject().get("platforms").toString(),
        new TypeToken<List<MobyGames.Platform>>(){}.getType());

    message("parsed " + platforms.size() + " platforms");

    Set<String> set1 = new HashSet<>();
    Set<String> set2 = new HashSet<>();

    /* try to match platform ids to actual systems */
    for (Platform platform : Platforms.values())
    {
      boolean found = false;
      set1.clear();
      String[] tokens = platform.fullName().toLowerCase().split(" ");
      // set1.addAll(tokens);

      for (MobyGames.Platform mplatform : platforms)
      {
        if (platform.fullName().toLowerCase().equals(mplatform.platform_name.toLowerCase()))
        {
          message("Found match for " + platform.fullName());
          found = true;
          break;
        }
      }

      if (!found)
        warning("Didn't found a match for " + platform.fullName());
    }

  }

  public MobyGamesFetcher()
  {
    loadPlatforms();
  }

  @Override
  public PluginInfo getInfo()
  {
    return new PluginInfo("MobyGames Data Fetcher", new PluginVersion(1, 0), "Jack",
        "This plugin gathers game data from MobyGames API.");
  }

  @Override
  public List<Attribute> supportedAttributes()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean supportsAssetDownload()
  {
    return true;
  }

  @Override
  public void searchAssetsForGame(Game game)
  {
    if (platformMapping.containsKey(game.getPlatform()))
    {
      int id = findGameID(game);
      System.out.println("game id: " + id);

      if (id != -1)
      {
        try
        {
          Thread.sleep(1000);

          var data = httpRequestToJson("https://api.mobygames.com/v1/games/" + id + "/platforms/"
              + platformMapping.get(game.getPlatform()) + "/screenshots", "api_key", API_KEY);

          Gson gson = new Gson();
          JsonObject root = gson.fromJson(data, JsonObject.class);
          List<MobyGames.Sample> array = gson.fromJson(root.get("screenshots"), new TypeToken<List<MobyGames.Sample>>(){}.getType());
          
          if (!array.isEmpty())
          {
            for (Sample sample : array)
              System.out.println(sample.image+" "+sample.caption);
            
            Sample first = array.get(0);
            
            try (InputStream in = new URL(first.image).openStream())
            {
              MessageDigest md = MessageDigest.getInstance("SHA-1");
    
              /* seed */
              byte[] bytes = new byte[20];
              SecureRandom.getInstanceStrong().nextBytes(bytes);
              
              //TODO maybe it's even too much
              /* calculate asset name with 20 random bytes + hash of the image itself */
              md.update(bytes);
              String fileName = StringUtils.toHexString(md.digest(first.image.getBytes())).toLowerCase() + "." + FileUtils.pathExtension(first.image);
              
              Path destination = Paths.get("data/", game.getGameSet().uuid().asPath(), "assets", fileName);
              
              Files.createDirectories(destination.getParent());
              Files.copy(in, destination);
            } 
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
        } catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }

      if (true)
        return;

      String title = game.getNormalizedTitle();
      StringBuilder query = new StringBuilder();

      for (int i = 0; i < title.length(); ++i)
      {
        char c = title.charAt(i);

        if (Character.isAlphabetic(c))
          query.append(c);
        else
          query.append(' ');
      }

      /* generate url */
      var data = httpRequestToJson("https://api.mobygames.com/v1/games", "api_key", API_KEY, "platform",
          Integer.toString(platformMapping.get(game.getPlatform())), "title", query.toString().trim());

      Gson gson = new Gson();
      JsonObject root = gson.fromJson(data, JsonObject.class);
      List<MobyGames.Game> array = gson.fromJson(root.get("games"), new TypeToken<List<MobyGames.Game>>(){}.getType());

      for (int i = 0; i < array.size(); ++i)
      {
        System.out.println(array.get(i).title);
      }

      MobyGames.Game result = array.get(0);

      {
        String imageURL = result.sample_cover.thumbnail_image;
        System.out.println(imageURL);

        if (!result.sample_screenshots.isEmpty())
        {
          for (Sample sample : result.sample_screenshots)
            System.out.println(sample.image+" "+sample.caption);
        }
      }
    }
  }

  public class ResultChoosePanel extends JPanel
  {

  }
}
