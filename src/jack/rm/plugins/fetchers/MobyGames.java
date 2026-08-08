package jack.rm.plugins.fetchers;

import java.util.Collections;
import java.util.List;

final class MobyGames
{
  static final class Platform
  {
    int platform_id;
    String platform_name;
  }

  static final class Game
  {
    int game_id;
    String title;

    @Override public String toString()
    {
      return title;
    }
  }

  static final class Games
  {
    List<Game> games = Collections.emptyList();
  }

  static final class Platforms
  {
    List<Platform> platforms = Collections.emptyList();
  }

  static final class Cover
  {
    String comments;
    String description;
    int width;
    int height;
    String image;
    String thumbnail_image;
    String scan_of;
  }

  static final class CoverGroup
  {
    String comments;
    List<String> countries = Collections.emptyList();
    List<Cover> covers = Collections.emptyList();
  }

  static final class CoverGroups
  {
    List<CoverGroup> cover_groups = Collections.emptyList();
  }

  private MobyGames()
  {
  }
}
