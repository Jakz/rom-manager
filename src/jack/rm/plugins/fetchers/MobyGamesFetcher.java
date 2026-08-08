package jack.rm.plugins.fetchers;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.assets.AssetKind;
import com.github.jakz.romlib.data.assets.AssetType;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.Main;
import jack.rm.plugins.types.DataFetcherPlugin;

public class MobyGamesFetcher extends DataFetcherPlugin
{
  private static final String API_BASE = "https://api.mobygames.com/v1";
  private static final Path LEGACY_API_KEY_FILE = Paths.get("mobykey.txt");

  private static final Map<Platform, List<String>> PLATFORM_NAMES = new LinkedHashMap<>();
  private final Map<Platform, Integer> platformIds = new LinkedHashMap<>();
  private long lastRequestAt;

  @ExposedParameter(name="API Key or File") private String apiKey = "";
  @ExposedParameter(name="Request Delay (ms)") private int requestDelayMillis = 1000;

  static
  {
    PLATFORM_NAMES.put(Platforms.A2600, List.of("Atari 2600"));
    PLATFORM_NAMES.put(Platforms.LYNX, List.of("Atari Lynx"));
    PLATFORM_NAMES.put(Platforms.AMIGA, List.of("Amiga"));
    PLATFORM_NAMES.put(Platforms.GB, List.of("Game Boy"));
    PLATFORM_NAMES.put(Platforms.GBC, List.of("Game Boy Color"));
    PLATFORM_NAMES.put(Platforms.GBA, List.of("Game Boy Advance"));
    PLATFORM_NAMES.put(Platforms.GC, List.of("GameCube", "Nintendo GameCube"));
    PLATFORM_NAMES.put(Platforms.GG, List.of("Game Gear"));
    PLATFORM_NAMES.put(Platforms.MD, List.of("Genesis", "Mega Drive", "Genesis / Mega Drive"));
    PLATFORM_NAMES.put(Platforms.NES, List.of("NES", "Nintendo Entertainment System"));
    PLATFORM_NAMES.put(Platforms.SNES, List.of("SNES", "Super Nintendo Entertainment System"));
    PLATFORM_NAMES.put(Platforms.N64, List.of("Nintendo 64"));
    PLATFORM_NAMES.put(Platforms.NDS, List.of("Nintendo DS"));
    PLATFORM_NAMES.put(Platforms._3DS, List.of("Nintendo 3DS"));
    PLATFORM_NAMES.put(Platforms.SWITCH, List.of("Nintendo Switch"));
    PLATFORM_NAMES.put(Platforms.NGP, List.of("Neo Geo Pocket", "Neo Geo Pocket Color"));
    PLATFORM_NAMES.put(Platforms.PS1, List.of("PlayStation"));
    PLATFORM_NAMES.put(Platforms.PS2, List.of("PlayStation 2"));
    PLATFORM_NAMES.put(Platforms.PSP, List.of("PSP", "PlayStation Portable"));
    PLATFORM_NAMES.put(Platforms.WS, List.of("WonderSwan", "WonderSwan Color"));
    PLATFORM_NAMES.put(Platforms.IBM_PC, List.of("DOS"));
  }

  @Override public PluginInfo getInfo()
  {
    return new PluginInfo("MobyGames Media Fetcher", new PluginVersion(1, 1), "Jack",
        "Downloads physical cartridge, disc and floppy scans classified as Media by MobyGames.");
  }

  @Override public List<Attribute> supportedAttributes()
  {
    return Collections.emptyList();
  }

  @Override public boolean supportsAssetDownload()
  {
    return true;
  }

  @Override public void searchAssetsForGame(Game game, AssetType type)
  {
    if (type != AssetType.IMAGE)
    {
      logDebug("Skipping unsupported asset type: " + type);
      return;
    }

    Asset cartridge = findCartridgeAsset(game);
    if (cartridge == null)
    {
      logWarning("No cartridge-label image asset is configured for " + game.getGameSet());
      return;
    }

    Optional<String> key = loadApiKey();
    if (!key.isPresent())
    {
      showMessage("Configure the MobyGames API key (or a key-file path) in the plugin settings.",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    setBusy(true);
    logDebug("MobyGames API credential loaded (" + key.get().length() + " characters)");
    logInfo("Searching MobyGames physical media for " + game.getTitle());

    new SwingWorker<List<MobyGames.Game>, Void>() {
      @Override protected List<MobyGames.Game> doInBackground() throws Exception
      {
        int platformId = resolvePlatformId(key.get(), game.getPlatform());
        return searchGames(key.get(), platformId, game);
      }

      @Override protected void done()
      {
        setBusy(false);

        try
        {
          List<MobyGames.Game> games = get();
          if (games.isEmpty())
          {
            showMessage("MobyGames found no matching game for " + game.getTitle() + ".",
                JOptionPane.INFORMATION_MESSAGE);
            return;
          }

          MobyGames.Game selected = chooseGame(game, games);
          if (selected != null)
            searchMedia(game, cartridge, key.get(), selected);
        }
        catch (Exception e)
        {
          handleRequestFailure("MobyGames search", e);
        }
      }
    }.execute();
  }

  private void searchMedia(Game game, Asset cartridge, String key, MobyGames.Game selectedGame)
  {
    setBusy(true);

    new SwingWorker<List<AssetFetchSelectionDialog.Result>, Void>() {
      @Override protected List<AssetFetchSelectionDialog.Result> doInBackground() throws Exception
      {
        int platformId = resolvePlatformId(key, game.getPlatform());
        return loadMediaResults(key, platformId, selectedGame);
      }

      @Override protected void done()
      {
        setBusy(false);

        try
        {
          List<AssetFetchSelectionDialog.Result> results = get();
          if (results.isEmpty())
          {
            showMessage("MobyGames has no physical media scan for this game and platform.",
                JOptionPane.INFORMATION_MESSAGE);
            return;
          }

          AssetFetchSelectionDialog.Selection selection = AssetFetchSelectionDialog.choose(Main.mainFrame,
              "Choose MobyGames cartridge/disc label", results, entry -> true);
          if (selection != null && !selection.entries.isEmpty())
            downloadAsync(game, cartridge, selection.entries.get(0));
        }
        catch (Exception e)
        {
          handleRequestFailure("MobyGames media search", e);
        }
      }
    }.execute();
  }

  private Asset findCartridgeAsset(Game game)
  {
    for (Asset asset : game.getGameSet().getAssetManager().getSupportedAssets())
      if (asset.getType() == AssetType.IMAGE && asset.getKind() == AssetKind.CARTRIDGE)
        return asset;

    return null;
  }

  private Optional<String> loadApiKey()
  {
    String configured = apiKey == null ? "" : apiKey.trim();

    if (!configured.isEmpty())
    {
      try
      {
        Path configuredPath = Paths.get(configured);
        if (Files.isRegularFile(configuredPath))
          return firstNonEmptyLine(configuredPath);
      }
      catch (InvalidPathException e)
      {
        // A literal API key is not required to be a valid local path.
      }

      return Optional.of(configured);
    }

    if (Files.isRegularFile(LEGACY_API_KEY_FILE))
      return firstNonEmptyLine(LEGACY_API_KEY_FILE);

    return Optional.empty();
  }

  private Optional<String> firstNonEmptyLine(Path path)
  {
    try
    {
      return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
          .map(String::trim)
          .filter(line -> !line.isEmpty())
          .findFirst();
    }
    catch (IOException e)
    {
      logWarning("Unable to read the MobyGames API key file " + path.toAbsolutePath());
      return Optional.empty();
    }
  }

  private synchronized void throttle() throws InterruptedException
  {
    long delay = Math.max(1000, requestDelayMillis);
    long wait = delay - (System.currentTimeMillis() - lastRequestAt);
    if (wait > 0)
      Thread.sleep(wait);
    lastRequestAt = System.currentTimeMillis();
  }

  private JsonElement requestJson(String endpoint, String key, String... args) throws IOException, InterruptedException
  {
    throttle();

    StringBuilder completeURL = new StringBuilder(API_BASE).append(endpoint).append("?api_key=").append(encode(key));
    for (int i = 0; i + 1 < args.length; i += 2)
      completeURL.append('&').append(encode(args[i])).append('=').append(encode(args[i + 1]));

    logDebug("MobyGames request: " + endpoint);
    HttpURLConnection connection = (HttpURLConnection)new URL(completeURL.toString()).openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    connection.setConnectTimeout(10000);
    connection.setReadTimeout(30000);

    int responseCode = connection.getResponseCode();
    InputStream stream = responseCode >= 200 && responseCode < 300
        ? connection.getInputStream() : connection.getErrorStream();
    String response = readString(stream);
    connection.disconnect();

    if (responseCode < 200 || responseCode >= 300)
      throw new ApiException(responseCode, "HTTP " + responseCode + apiErrorSuffix(response));

    return new JsonParser().parse(response);
  }

  private String readString(InputStream stream) throws IOException
  {
    if (stream == null)
      return "";

    StringBuilder output = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)))
    {
      String line;
      while ((line = reader.readLine()) != null)
        output.append(line).append('\n');
    }
    return output.toString();
  }

  private String apiErrorSuffix(String response)
  {
    if (response == null || response.trim().isEmpty())
      return "";

    try
    {
      JsonElement json = new JsonParser().parse(response);
      if (json.isJsonObject() && json.getAsJsonObject().has("message"))
        return ": " + json.getAsJsonObject().get("message").getAsString();
    }
    catch (Exception e)
    {
      // Keep malformed server responses out of logs; they may contain HTML or unrelated data.
    }
    return "";
  }

  private String encode(String value)
  {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private int resolvePlatformId(String key, Platform platform) throws IOException, InterruptedException
  {
    Integer cached = platformIds.get(platform);
    if (cached != null)
      return cached;

    List<String> expectedNames = PLATFORM_NAMES.get(platform);
    if (expectedNames == null)
      throw new IOException("MobyGames platform is not mapped: " + platform.fullName());

    MobyGames.Platforms response = new Gson().fromJson(requestJson("/platforms", key), MobyGames.Platforms.class);
    if (response == null || response.platforms == null)
      throw new IOException("MobyGames returned no platform list");

    for (MobyGames.Platform candidate : response.platforms)
      if (candidate.platform_name != null && expectedNames.stream().anyMatch(candidate.platform_name::equalsIgnoreCase))
      {
        platformIds.put(platform, candidate.platform_id);
        return candidate.platform_id;
      }

    throw new IOException("MobyGames platform was not found: " + platform.fullName());
  }

  private List<MobyGames.Game> searchGames(String key, int platformId, Game game)
      throws IOException, InterruptedException
  {
    for (String query : titleQueries(game.getNormalizedTitle(), game.getTitle(), game.getExportTitle(),
        game.getCorrectName()))
    {
      logDebug("MobyGames title query: " + query);
      MobyGames.Games response = new Gson().fromJson(requestJson("/games", key,
          "platform", Integer.toString(platformId), "format", "brief", "title", query), MobyGames.Games.class);

      if (response != null && response.games != null && !response.games.isEmpty())
        return response.games;
    }

    return Collections.emptyList();
  }

  static List<String> titleQueries(String... titles)
  {
    List<String> queries = new ArrayList<>();

    for (String title : titles)
    {
      String stripped = stripDatTags(title);
      addQuery(queries, stripped);
      addQuery(queries, simplifyTitle(stripped));
    }

    return queries;
  }

  private static String stripDatTags(String title)
  {
    if (title == null)
      return "";

    int parenthesis = title.indexOf('(');
    int bracket = title.indexOf('[');
    int end = title.length();

    if (parenthesis >= 0)
      end = Math.min(end, parenthesis);
    if (bracket >= 0)
      end = Math.min(end, bracket);

    return title.substring(0, end).trim().replaceAll("\\s+", " ");
  }

  private static String simplifyTitle(String title)
  {
    if (title == null)
      return "";

    StringBuilder query = new StringBuilder();
    for (int i = 0; i < title.length(); ++i)
    {
      char c = title.charAt(i);
      if (Character.isLetterOrDigit(c))
        query.append(c);
      else if (query.length() != 0 && query.charAt(query.length() - 1) != ' ')
        query.append(' ');
    }
    return query.toString().trim();
  }

  private static void addQuery(List<String> queries, String query)
  {
    if (query != null && !query.isEmpty() && !queries.contains(query))
      queries.add(query);
  }

  private MobyGames.Game chooseGame(Game source, List<MobyGames.Game> games)
  {
    if (games.size() == 1)
      return games.get(0);

    MobyGames.Game initial = games.stream()
        .filter(candidate -> candidate.title != null && candidate.title.equalsIgnoreCase(source.getTitle()))
        .findFirst()
        .orElse(games.get(0));

    return (MobyGames.Game)JOptionPane.showInputDialog(Main.mainFrame,
        "MobyGames returned more than one title. Choose the matching game:", "Choose MobyGames game",
        JOptionPane.PLAIN_MESSAGE, null, games.toArray(), initial);
  }

  List<AssetFetchSelectionDialog.Result> loadMediaResults(String key, int platformId, MobyGames.Game game)
      throws IOException, InterruptedException
  {
    MobyGames.CoverGroups response = new Gson().fromJson(requestJson(
        "/games/" + game.game_id + "/platforms/" + platformId + "/covers", key), MobyGames.CoverGroups.class);
    return mediaResults(game, response);
  }

  static List<AssetFetchSelectionDialog.Result> mediaResults(MobyGames.Game game, MobyGames.CoverGroups response)
  {
    List<AssetFetchSelectionDialog.Result> results = new ArrayList<>();
    if (response == null || response.cover_groups == null)
      return results;

    for (MobyGames.CoverGroup group : response.cover_groups)
    {
      if (group == null || group.covers == null)
        continue;

      int mediaIndex = 0;
      for (MobyGames.Cover cover : group.covers)
      {
        if (cover == null || cover.scan_of == null || !cover.scan_of.equalsIgnoreCase("Media") || cover.image == null)
          continue;

        ++mediaIndex;
        try
        {
          String countries = group.countries == null || group.countries.isEmpty()
              ? "Unknown region" : String.join(", ", group.countries);
          String details = firstPresent(cover.description, cover.comments, group.comments);
          String suffix = details == null ? "" : " - " + details;
          String name = game.title + " - " + countries + (mediaIndex > 1 ? " - Media " + mediaIndex : "") + suffix;
          AssetFetchSelectionDialog.Entry entry = new AssetFetchSelectionDialog.Entry(AssetKind.CARTRIDGE,
              mediaUrl(cover.image));
          results.add(new AssetFetchSelectionDialog.Result(name, 0, List.of(entry)));
        }
        catch (Exception e)
        {
          // Ignore malformed image URLs while preserving other cover groups.
        }
      }
    }

    return results;
  }

  private static String firstPresent(String... values)
  {
    for (String value : values)
      if (value != null && !value.trim().isEmpty())
        return value.trim();
    return null;
  }

  private static URL mediaUrl(String value) throws IOException
  {
    URL url = new URL(value);
    if (url.getProtocol().equalsIgnoreCase("http") && url.getHost().toLowerCase().endsWith("mobygames.com"))
      return new URL("https", url.getHost(), url.getPort(), url.getFile());
    return url;
  }

  private void downloadAsync(Game game, Asset asset, AssetFetchSelectionDialog.Entry entry)
  {
    setBusy(true);

    new SwingWorker<Boolean, Void>() {
      @Override protected Boolean doInBackground()
      {
        return download(game, asset, entry);
      }

      @Override protected void done()
      {
        setBusy(false);
        try
        {
          if (get() && Main.mainFrame != null)
            Main.mainFrame.updateInfoPanel(game);
        }
        catch (Exception e)
        {
          logWarning("Unable to finish MobyGames media download: " + e.getMessage());
        }
      }
    }.execute();
  }

  private boolean download(Game game, Asset asset, AssetFetchSelectionDialog.Entry entry)
  {
    try
    {
      BufferedImage image = ImageIO.read(entry.url);
      if (image == null)
        throw new IOException("unsupported image format");

      AssetData data = game.getAssetData(asset);
      data.setPath(Paths.get(Asset.safeName(game.getCorrectName()) + ".png"));
      Path destination = data.getFinalPath();
      Files.createDirectories(destination.getParent());

      if (!ImageIO.write(image, "png", destination.toFile()))
        throw new IOException("PNG writer is unavailable");

      data.setCRC(FileUtils.calculateCRCFast(destination));
      data.setURLData(entry.url.toString());
      logInfo("Downloaded MobyGames physical media for " + game.getTitle() + " to " + destination.toAbsolutePath());

      return true;
    }
    catch (Exception e)
    {
      logWarning("Unable to download MobyGames media: " + e.getMessage());
      return false;
    }
  }

  private void setBusy(boolean busy)
  {
    if (Main.mainFrame != null)
      Main.mainFrame.setCursor(Cursor.getPredefinedCursor(busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
  }

  private void showMessage(String text, int messageType)
  {
    logInfo(text);
    JOptionPane.showMessageDialog(Main.mainFrame, text, getInfo().name, messageType);
  }

  private void handleRequestFailure(String action, Exception exception)
  {
    Throwable cause = exception;
    while (cause instanceof ExecutionException && cause.getCause() != null)
      cause = cause.getCause();

    logWarning(action + " failed: " + cause.getMessage());

    if (cause instanceof ApiException && ((ApiException)cause).statusCode == HttpURLConnection.HTTP_UNAUTHORIZED)
    {
      showMessage("MobyGames rejected the configured API key. Verify that it is the private key from your "
          + "MobyGames API page and that your current subscription includes API access.", JOptionPane.ERROR_MESSAGE);
    }
    else
      showMessage(action + " failed: " + cause.getMessage(), JOptionPane.ERROR_MESSAGE);
  }

  private static final class ApiException extends IOException
  {
    private static final long serialVersionUID = 1L;
    final int statusCode;

    ApiException(int statusCode, String message)
    {
      super(message);
      this.statusCode = statusCode;
    }
  }

  private String logSafe(String message)
  {
    return message.replace("%", "%%");
  }

  private void logDebug(String message)
  {
    debug(logSafe(message));
  }

  private void logInfo(String message)
  {
    message(logSafe(message));
  }

  private void logWarning(String message)
  {
    warning(logSafe(message));
  }
}
