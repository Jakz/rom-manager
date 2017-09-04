package jack.rm.plugins.types;

import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

// TODO: better name?
public abstract class FormatSupportPlugin extends ActualPlugin
{
  @Override public final boolean isNative() { return false; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.FORMAT_SUPPORT; }
  
  public abstract Handle getSpecializedEntry(Handle entry);
}
