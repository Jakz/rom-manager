package jack.rm.files.parser;

import com.github.jakz.romlib.data.game.GameSave;

@FunctionalInterface
public interface SaveParser
{
  public GameSave<?> parse(String string);
}
