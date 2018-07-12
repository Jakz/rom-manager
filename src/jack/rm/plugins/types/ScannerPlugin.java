package jack.rm.plugins.types;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

/**
 * This plugin type is meant t
 * @author jack
 *
 */
public abstract class ScannerPlugin extends ActualPlugin
{  
  @Override public final boolean isNative() { return false; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.SCANNER; }
  
  protected final Predicate<GameSet> compatibility() { return rs -> true; } // TODO: real compatibility
  
  abstract public String[] getHandledExtensions();
  abstract public List<VerifierEntry> scanFile(Path path) throws IOException;
  
  //@Override public int compareTo(ScannerPlugin other) { return Integer.compare(getPriority(), other.getPriority()); }
}
