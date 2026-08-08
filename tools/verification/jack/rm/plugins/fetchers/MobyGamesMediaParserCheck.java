package jack.rm.plugins.fetchers;

import java.util.List;

import com.google.gson.Gson;

public final class MobyGamesMediaParserCheck
{
  public static void main(String[] args)
  {
    String json = "{\"cover_groups\":["
        + "{\"countries\":[\"Italy\"],\"covers\":["
        + "{\"scan_of\":\"Front Cover\",\"image\":\"https://example.test/front.jpg\"},"
        + "{\"scan_of\":\"Media\",\"description\":\"Cartridge\","
        + "\"image\":\"https://example.test/cart.jpg\"}]},"
        + "{\"countries\":[\"United States\"],\"covers\":["
        + "{\"scan_of\":\"Media\",\"image\":\"https://example.test/disc.jpg\"}]}]}";

    MobyGames.Game game = new MobyGames.Game();
    game.title = "Test Game";
    MobyGames.CoverGroups groups = new Gson().fromJson(json, MobyGames.CoverGroups.class);
    List<AssetFetchSelectionDialog.Result> results = MobyGamesFetcher.mediaResults(game, groups);

    require(results.size() == 2, "expected exactly two physical-media results");
    require(results.get(0).name.equals("Test Game - Italy - Cartridge"), "unexpected first result caption");
    require(results.get(0).entries.size() == 1, "each result must select one physical medium");
    require(results.get(0).entries.get(0).url.toString().endsWith("/cart.jpg"), "front cover was not filtered");
    require(results.get(1).name.equals("Test Game - United States"), "unexpected region caption");

    List<String> titleQueries = MobyGamesFetcher.titleQueries("Super Mario Bros. 3",
        "Super Mario Bros. 3 (Europe)");
    require(titleQueries.get(0).equals("Super Mario Bros. 3"), "title punctuation must be preserved");
    require(!titleQueries.contains("Super Mario Bros. 3 (Europe)"), "DAT region tags must be removed");
    require(titleQueries.contains("Super Mario Bros 3"), "a punctuation-light fallback must be available");

    System.out.println("MobyGames physical-media parser: OK");
  }

  private static void require(boolean condition, String message)
  {
    if (!condition)
      throw new AssertionError(message);
  }

  private MobyGamesMediaParserCheck()
  {
  }
}
