package jack.rm.plugins.scanners;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;

import com.pixbits.plugin.PluginType;

import jack.rm.data.romset.RomHashFinder;
import jack.rm.data.romset.RomSet;
import jack.rm.files.ScanResult;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class ScannerPlugin extends ActualPlugin implements Comparable<ScannerPlugin>
{
  @Override public final boolean isNative() { return true; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.SCANNER; }
  
  protected abstract int getPriority();
  protected final Predicate<RomSet> compatibility() { return rs -> false; }
  
  abstract public String[] getHandledExtensions();
  abstract public ScanResult scanRom(RomHashFinder finder, Path file);
  
  @Override public int compareTo(ScannerPlugin other) { return Integer.compare(getPriority(), other.getPriority()); }
}
