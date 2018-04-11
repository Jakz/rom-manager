package jack.rm.plugins.types;

import java.util.List;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.files.Pattern;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class PatternSetPlugin extends ActualPlugin
{
  public PluginType<?> getPluginType() { return PluginRealType.PATTERN_SET; }
  
  public abstract List<Pattern<Game>> getPatterns();
}
