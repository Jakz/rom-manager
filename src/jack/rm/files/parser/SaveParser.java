package jack.rm.files.parser;

import com.github.jakz.romlib.data.game.RomSave;

@FunctionalInterface
public interface SaveParser
{
  public RomSave<?> parse(String string);
}
