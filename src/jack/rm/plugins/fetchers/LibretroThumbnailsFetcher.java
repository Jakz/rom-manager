package jack.rm.plugins.fetchers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jakz.romlib.data.assets.Asset;
import com.github.jakz.romlib.data.assets.AssetData;
import com.github.jakz.romlib.data.assets.AssetKind;
import com.github.jakz.romlib.data.assets.AssetType;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.Main;
import jack.rm.plugins.types.DataFetcherPlugin;

public class LibretroThumbnailsFetcher extends DataFetcherPlugin
{
  private static final String BASE_URL = "https://thumbnails.libretro.com";
  private static final int MAX_MATCHES = 25;

  private static final Map<Platform, String> PLATFORM_MAPPING = new LinkedHashMap<>();
  private static final Map<AssetKind, String> ASSET_FOLDER_MAPPING = new LinkedHashMap<>();
  private final Map<String, List<String>> indexCache = new HashMap<>();

  @ExposedParameter(name="Fuzzy Match Mode") private AssetFetchSelectionDialog.MatchMode matchMode = AssetFetchSelectionDialog.MatchMode.BALANCED;

  static
  {
    PLATFORM_MAPPING.put(Platforms.NES, "Nintendo - Nintendo Entertainment System");
    PLATFORM_MAPPING.put(Platforms.SNES, "Nintendo - Super Nintendo Entertainment System");
    PLATFORM_MAPPING.put(Platforms.N64, "Nintendo - Nintendo 64");
    PLATFORM_MAPPING.put(Platforms.GB, "Nintendo - Game Boy");
    PLATFORM_MAPPING.put(Platforms.GBC, "Nintendo - Game Boy Color");
    PLATFORM_MAPPING.put(Platforms.GBA, "Nintendo - Game Boy Advance");
    PLATFORM_MAPPING.put(Platforms.NDS, "Nintendo - Nintendo DS");
    PLATFORM_MAPPING.put(Platforms._3DS, "Nintendo - Nintendo 3DS");
    PLATFORM_MAPPING.put(Platforms.PS1, "Sony - PlayStation");
    PLATFORM_MAPPING.put(Platforms.PS2, "Sony - PlayStation 2");
    PLATFORM_MAPPING.put(Platforms.PSP, "Sony - PlayStation Portable");
    PLATFORM_MAPPING.put(Platforms.MD, "Sega - Mega Drive - Genesis");
    PLATFORM_MAPPING.put(Platforms.GG, "Sega - Game Gear");
    PLATFORM_MAPPING.put(Platforms.A2600, "Atari - 2600");
    PLATFORM_MAPPING.put(Platforms.LYNX, "Atari - Lynx");
    PLATFORM_MAPPING.put(Platforms.MAME, "MAME");
    PLATFORM_MAPPING.put(Platforms.IBM_PC, "DOS");

    ASSET_FOLDER_MAPPING.put(AssetKind.BOXART, "Named_Boxarts");
    ASSET_FOLDER_MAPPING.put(AssetKind.TITLE_SCREEN, "Named_Titles");
    ASSET_FOLDER_MAPPING.put(AssetKind.GAMEPLAY_SCREEN, "Named_Snaps");
  }

  @Override
  public PluginInfo getInfo()
  {
    return new PluginInfo("Libretro Thumbnail Fetcher", new PluginVersion(1, 0), "Jack",
        "This plugin downloads screenshot thumbnails from the free Libretro thumbnail server.");
  }

  @Override
  public List<Attribute> supportedAttributes()
  {
    return Collections.emptyList();
  }

  @Override
  public boolean supportsAssetDownload()
  {
    return true;
  }

  @Override
  public void searchAssetsForGame(Game game, AssetType type)
  {
    if (type != AssetType.IMAGE)
    {
      logDebug("Skipping asset search for unsupported asset type: " + type);
      return;
    }

    Map<AssetKind, Asset> assets = imageAssetsByKind(game);
    if (assets.isEmpty())
    {
      logWarning("No image asset is configured for " + game.getGameSet());
      return;
    }

    Optional<String> platform = Optional.ofNullable(PLATFORM_MAPPING.get(game.getPlatform()));
    if (!platform.isPresent())
    {
      logWarning("Libretro thumbnail platform not mapped: " + game.getPlatform().fullName());
      return;
    }

    AssetFetchSelectionDialog.MatchMode initialMode = matchMode != null ? matchMode : AssetFetchSelectionDialog.MatchMode.BALANCED;

    List<AssetFetchSelectionDialog.Result> candidates = buildCandidates(platform.get(), game, assets, initialMode);
    logInfo("Libretro candidates for " + game.getTitle() + ": " + candidates.size());

    AssetFetchSelectionDialog.Selection selection = AssetFetchSelectionDialog.choose(Main.mainFrame,
        "Choose assets for " + game.getTitle(), initialMode, mode -> {
          matchMode = mode;
          return buildCandidates(platform.get(), game, assets, mode);
        },
        this::assetExists);

    if (selection != null)
    {
      matchMode = selection.matchMode;
      download(game, assets, selection);
    }
    else
      logInfo("Libretro asset download cancelled for " + game.getTitle());
  }

  private Map<AssetKind, Asset> imageAssetsByKind(Game game)
  {
    Map<AssetKind, Asset> assets = new LinkedHashMap<>();

    for (Asset asset : game.getGameSet().getAssetManager().getSupportedAssets())
      if (asset.getType() == AssetType.IMAGE)
        assets.put(asset.getKind(), asset);

    return assets;
  }

  private List<AssetFetchSelectionDialog.Result> buildCandidates(String platform, Game game, Map<AssetKind, Asset> assets,
      AssetFetchSelectionDialog.MatchMode mode)
  {
    logInfo("Searching Libretro thumbnails for " + game.getTitle() + " on " + platform);

    List<String> names = new ArrayList<>();
    addCandidateName(names, game.getTitle());
    addCandidateName(names, game.getNormalizedTitle());
    addCandidateName(names, game.getExportTitle());
    addCandidateName(names, game.getCorrectName());

    logDebug("Direct thumbnail candidate names: " + names);

    List<AssetFetchSelectionDialog.Result> candidates = new ArrayList<>();
    for (String name : names)
      candidates.add(buildResult(platform, name, 0, assets));

    candidates.addAll(matchIndexedCandidates(platform, names, assets, mode));

    return candidates.stream()
        .collect(Collectors.toMap(candidate -> candidate.name, candidate -> candidate, (left, right) -> left,
            LinkedHashMap::new))
        .values()
        .stream()
        .sorted(Comparator.comparingInt(candidate -> candidate.score))
        .collect(Collectors.toList());
  }

  private AssetFetchSelectionDialog.Result buildResult(String platform, String name, int score, Map<AssetKind, Asset> assets)
  {
    List<AssetFetchSelectionDialog.Entry> entries = assets.keySet().stream()
        .filter(ASSET_FOLDER_MAPPING::containsKey)
        .map(kind -> new AssetFetchSelectionDialog.Entry(kind, urlFor(platform, ASSET_FOLDER_MAPPING.get(kind), name)))
        .collect(Collectors.toList());

    return new AssetFetchSelectionDialog.Result(name, score, entries);
  }

  private List<AssetFetchSelectionDialog.Result> matchIndexedCandidates(String platform, List<String> names,
      Map<AssetKind, Asset> assets, AssetFetchSelectionDialog.MatchMode mode)
  {
    try
    {
      List<String> index = loadIndex(platform);
      List<String> normalizedQueries = names.stream()
          .map(this::normalizeForMatch)
          .filter(name -> !name.isEmpty())
          .collect(Collectors.toList());
      logDebug("Normalized Libretro fuzzy queries: " + normalizedQueries);

      List<AssetFetchSelectionDialog.Result> matches = index.stream()
          .map(name -> buildResult(platform, name, score(name, normalizedQueries, mode), assets))
          .filter(candidate -> candidate.score < Integer.MAX_VALUE)
          .sorted(Comparator.comparingInt(candidate -> candidate.score))
          .limit(MAX_MATCHES)
          .collect(Collectors.toList());

      logInfo("Libretro fuzzy matches for " + names.get(0) + ": " + matches.size());
      if (!matches.isEmpty())
        logDebug("Top Libretro fuzzy matches: " + matches);

      return matches;
    }
    catch (Exception e)
    {
      logWarning("Unable to load Libretro thumbnail index for " + platform + ": " + e.getMessage());
      return Collections.emptyList();
    }
  }

  private List<String> loadIndex(String platform) throws Exception
  {
    List<String> cached = indexCache.get(platform);
    if (cached != null)
      return cached;

    List<String> index = new ArrayList<>();

    for (String folder : ASSET_FOLDER_MAPPING.values())
    {
      URL url = new URL(BASE_URL + "/" + encodePath(platform) + "/" + folder + "/");
      logInfo("Loading Libretro thumbnail index: " + url);

      String html = readString(url);
      logDebug("Libretro thumbnail index bytes/chars read for " + folder + ": " + html.length());

      Pattern linkPattern = Pattern.compile("href\\s*=\\s*\"([^\"]+\\.png)\"", Pattern.CASE_INSENSITIVE);
      Matcher matcher = linkPattern.matcher(html);

      while (matcher.find())
        index.add(matcher.group(1));
    }

    index = index.stream()
        .map(this::decodeFileName)
        .map(name -> name.endsWith(".png") ? name.substring(0, name.length() - 4) : name)
        .distinct()
        .collect(Collectors.toList());

    indexCache.put(platform, index);
    logInfo("Loaded " + index.size() + " Libretro thumbnail names for " + platform);
    if (index.isEmpty())
      logWarning("Libretro index is empty for " + platform + ". Directory listing format may have changed.");

    return index;
  }

  private String readString(URL url) throws Exception
  {
    StringBuilder builder = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)))
    {
      String line;
      while ((line = reader.readLine()) != null)
        builder.append(line).append('\n');
    }

    return builder.toString();
  }

  private String decodeFileName(String name)
  {
    try
    {
      return URLDecoder.decode(name, java.nio.charset.StandardCharsets.UTF_8);
    }
    catch (Exception e)
    {
      return name;
    }
  }

  private int score(String candidate, List<String> normalizedQueries, AssetFetchSelectionDialog.MatchMode mode)
  {
    String normalizedCandidate = normalizeForMatch(candidate);
    if (normalizedCandidate.isEmpty())
      return Integer.MAX_VALUE;

    int best = Integer.MAX_VALUE;

    for (String query : normalizedQueries)
    {
      if (normalizedCandidate.equals(query))
        return 0;
      else if (normalizedCandidate.startsWith(query) || query.startsWith(normalizedCandidate))
        best = Math.min(best, 2);
      else if (normalizedCandidate.contains(query) || query.contains(normalizedCandidate))
        best = Math.min(best, 4);
      else
      {
        int tokenScore = tokenSubsetScore(normalizedCandidate, query, mode);
        if (tokenScore != Integer.MAX_VALUE)
          best = Math.min(best, tokenScore);

        int distance = levenshtein(normalizedCandidate, query);
        int maxLength = Math.max(normalizedCandidate.length(), query.length());

        double overlap = tokenOverlap(normalizedCandidate, query);
        double overlapThreshold = mode == AssetFetchSelectionDialog.MatchMode.STRICT ? 0.65
            : mode == AssetFetchSelectionDialog.MatchMode.BROAD ? 0.25 : 0.40;
        int distanceDivisor = mode == AssetFetchSelectionDialog.MatchMode.STRICT ? 4
            : mode == AssetFetchSelectionDialog.MatchMode.BROAD ? 2 : 3;

        if (overlap >= overlapThreshold)
          best = Math.min(best, 20 + distance - (int)(overlap * 10));
        else if (maxLength != 0 && distance <= Math.max(4, maxLength / distanceDivisor))
          best = Math.min(best, 40 + distance);
      }
    }

    return best;
  }

  private int tokenSubsetScore(String candidate, String query, AssetFetchSelectionDialog.MatchMode mode)
  {
    List<String> candidateTokens = significantTokens(candidate);
    List<String> queryTokens = significantTokens(query);

    if (candidateTokens.isEmpty() || queryTokens.isEmpty())
      return Integer.MAX_VALUE;

    long matches = queryTokens.stream().filter(candidateTokens::contains).count();

    if (matches == queryTokens.size())
      return 8 + candidateTokens.size() - queryTokens.size();

    if (mode == AssetFetchSelectionDialog.MatchMode.BROAD && matches != 0)
      return 25 + (queryTokens.size() - (int)matches) * 4 + candidateTokens.size();

    return Integer.MAX_VALUE;
  }

  private List<String> significantTokens(String value)
  {
    return List.of(value.split(" ")).stream()
        .filter(token -> !token.isEmpty())
        .filter(token -> !token.chars().allMatch(Character::isDigit))
        .filter(token -> token.length() > 1)
        .filter(token -> !isStopToken(token))
        .collect(Collectors.toList());
  }

  private boolean isStopToken(String token)
  {
    return token.equals("the") || token.equals("a") || token.equals("an") || token.equals("of") || token.equals("and")
        || token.equals("s");
  }

  private double tokenOverlap(String left, String right)
  {
    List<String> leftTokens = List.of(left.split(" ")).stream()
        .filter(token -> !token.isEmpty())
        .collect(Collectors.toList());
    List<String> rightTokens = List.of(right.split(" ")).stream()
        .filter(token -> !token.isEmpty())
        .collect(Collectors.toList());

    if (leftTokens.isEmpty() || rightTokens.isEmpty())
      return 0.0;

    long matches = leftTokens.stream().filter(rightTokens::contains).count();
    return matches / (double)Math.max(leftTokens.size(), rightTokens.size());
  }

  private String normalizeForMatch(String name)
  {
    StringBuilder builder = new StringBuilder();
    boolean skipParentheses = false;
    boolean skipBrackets = false;

    for (int i = 0; i < name.length(); ++i)
    {
      char c = Character.toLowerCase(name.charAt(i));

      if (c == '(')
        skipParentheses = true;
      else if (c == ')')
        skipParentheses = false;
      else if (c == '[')
        skipBrackets = true;
      else if (c == ']')
        skipBrackets = false;
      else if (!skipParentheses && !skipBrackets)
      {
        if (Character.isLetterOrDigit(c))
          builder.append(c);
        else if (builder.length() != 0 && builder.charAt(builder.length() - 1) != ' ')
          builder.append(' ');
      }
    }

    return stripLeadingCatalogId(builder.toString().trim());
  }

  private String stripLeadingCatalogId(String name)
  {
    return name.replaceFirst("^(?:\\d+\\s+)+", "").trim();
  }

  private int levenshtein(String left, String right)
  {
    int[] previous = new int[right.length() + 1];
    int[] current = new int[right.length() + 1];

    for (int i = 0; i <= right.length(); ++i)
      previous[i] = i;

    for (int i = 1; i <= left.length(); ++i)
    {
      current[0] = i;

      for (int j = 1; j <= right.length(); ++j)
      {
        int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
        current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + cost);
      }

      int[] swap = previous;
      previous = current;
      current = swap;
    }

    return previous[right.length()];
  }

  private void addCandidateName(List<String> names, String name)
  {
    if (name != null && !name.trim().isEmpty() && !names.contains(name))
      names.add(name);
  }

  private URL urlFor(String platform, String folder, String name)
  {
    try
    {
      String path = encodePath(platform) + "/" + folder + "/" + encodePath(Asset.safeName(name) + ".png");
      return new URL(BASE_URL + "/" + path);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private boolean assetExists(AssetFetchSelectionDialog.Entry entry)
  {
    try
    {
      HttpURLConnection conn = (HttpURLConnection)entry.url.openConnection();
      conn.setRequestMethod("HEAD");
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);
      int code = conn.getResponseCode();
      if (code >= 200 && code < 300)
        return true;

      if (code != HttpURLConnection.HTTP_BAD_METHOD)
        return false;
    }
    catch (Exception e)
    {
      // Some simple static servers do not implement HEAD reliably. Fall through to a small GET/open check.
    }

    try (InputStream ignored = entry.url.openStream())
    {
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  private String encodePath(String value)
  {
    return URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
  }

  private void download(Game game, Map<AssetKind, Asset> assets, AssetFetchSelectionDialog.Selection selection)
  {
    for (AssetFetchSelectionDialog.Entry entry : selection.entries)
    {
      Asset asset = assets.get(entry.kind);
      if (asset == null)
        continue;

      download(game, asset, entry);
    }

    Main.mainFrame.updateInfoPanel(game);
  }

  private void download(Game game, Asset asset, AssetFetchSelectionDialog.Entry entry)
  {
    try
    {
      Path fileName = Paths.get(Asset.safeName(game.getCorrectName()) + ".png");
      AssetData data = game.getAssetData(asset);
      data.setPath(fileName);

      Path destination = data.getFinalPath();
      logInfo("Saving Libretro thumbnail to " + destination.toAbsolutePath());
      Files.createDirectories(destination.getParent());

      try (InputStream input = entry.url.openStream())
      {
        Files.copy(input, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      }

      data.setCRC(FileUtils.calculateCRCFast(destination));
      data.setURLData(entry.url.toString());

      logInfo("Downloaded " + entry.kind.getCaption() + " for " + game.getTitle() + " from " + entry.url);
    }
    catch (Exception e)
    {
      logWarning("Unable to download Libretro thumbnail " + entry.url + ": " + e.getMessage());
      e.printStackTrace();
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
