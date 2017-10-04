package com.github.jakz.romlib.data.set;

import java.nio.file.Path;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.searcher.DummySearcher;
import com.pixbits.lib.searcher.Searcher;

public interface GameSetFeatures
{
  boolean hasFeature(Feature feature);
  Searcher<Game> searcher();
  Path getAttachmentPath();
  
  public static GameSetFeatures of(final GameSet set)
  {
    return new GameSetFeatures() {

      final Searcher<Game> searcher = new DummySearcher<>();
      
      @Override
      public boolean hasFeature(Feature feature)
      {
        return false;
      }

      @Override
      public Searcher<Game> searcher()
      {
        return searcher;
      }

      @Override
      public Path getAttachmentPath()
      {
        throw new UnsupportedOperationException("Can't get attachment path for dummy GameSetFeatures");
      }     
    };
  }
}
