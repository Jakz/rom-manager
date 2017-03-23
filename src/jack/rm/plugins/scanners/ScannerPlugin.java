package jack.rm.plugins.scanners;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Predicate;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.HandleSet;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class ScannerPlugin extends ActualPlugin
{  
  @Override public final boolean isNative() { return false; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.SCANNER; }
  
  protected final Predicate<GameSet> compatibility() { return rs -> true; } // TODO: real compatibility
  
  abstract public String[] getHandledExtensions();
  abstract public HandleSet scanFiles(Path path, Set<Path> ignoredPaths) throws IOException;
  
  //@Override public int compareTo(ScannerPlugin other) { return Integer.compare(getPriority(), other.getPriority()); }
}
