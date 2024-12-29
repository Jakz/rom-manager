package jack.rm.plugins.fetchers;

import java.util.List;

public class MobyGames
{
  public class Platform
  {
    int platform_id;
    String platform_name;
  }  
  
  public class Game
  {
    int game_id;
    String title;
    Sample sample_cover;
    List<Sample> sample_screenshots;
  }
  
  public class Sample
  {
    int width;
    int height;
    String image;
    String thumbnail_image;
    String caption;
  }
  
  public class GameIDs
  {
    List<Integer> games;
  } 
  
  public class Games
  {
    List<Game> games;
  }
}
