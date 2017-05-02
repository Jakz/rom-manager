package jack.rm.workflow;

import java.util.HashSet;
import java.util.Set;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.platforms.Platform;
import com.pixbits.lib.workflow.Mutuator;

public abstract class RomOperation implements Mutuator<GameEntry>
{  
  public static enum Mode
  {
    EXCLUDE,
    INCLUDE
  };
  
  private final Set<Game> specifics;
  private Mode mode;
  
  public RomOperation()
  {
    specifics = new HashSet<Game>();
    mode = Mode.EXCLUDE;
  }
  
  public void exclude(Game rom)
  {
    if (mode == Mode.EXCLUDE)
      specifics.add(rom);
    else
      throw new UnsupportedOperationException("Can't exclude a ROM from an operation which is in INCLUDE mode");
  }
  
  public void include(Game rom)
  {
    if (mode == Mode.INCLUDE || specifics.isEmpty())
    {
      mode = Mode.INCLUDE;
      specifics.add(rom);
    }
    else
      throw new UnsupportedOperationException("Can't include a ROM from an operation which is in EXCLUDE mode");
  }
  
  public boolean shouldBeProcessed(Game rom)
  {
    return !(mode == Mode.INCLUDE ^ specifics.contains(rom));
  }
  
  public abstract String getName();
  public abstract String getDescription();
  boolean isPlatformSupported(Platform platform) { return true; }
  
  public final GameEntry apply(GameEntry handle)
  {
    try
    {
      return shouldBeProcessed(handle.getGame()) ? doApply(handle) : handle;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return handle;
    }
  }
  
  abstract protected GameEntry doApply(GameEntry handle) throws Exception;
}
