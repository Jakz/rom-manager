package jack.rm.plugins.scanners;

import java.util.List;

import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.NestedArchiveBatch;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import jack.rm.files.ScanResult;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class VerifierPlugin extends ActualPlugin
{  
  @Override public final boolean isNative() { return false; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.VERIFIER; }
  
  public abstract void setup(RomSet set);
  public abstract List<ScanResult> verifyHandle(VerifierEntry handle) throws VerifierException;
}
