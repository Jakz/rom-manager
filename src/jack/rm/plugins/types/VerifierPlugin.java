package jack.rm.plugins.types;

import java.util.List;
import java.util.function.Function;

import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.files.ScanResult;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.scanners.VerifierException;

public abstract class VerifierPlugin extends ActualPlugin
{  
  @Override public final boolean isNative() { return false; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.VERIFIER; }
  
  public abstract void setup(GameSet set);
  
  public abstract void setEntryTransformer(Function<Handle, Handle> transformer);
  public abstract List<ScanResult> verifyHandle(VerifierEntry handle) throws VerifierException;
}
