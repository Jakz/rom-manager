package jack.rm.plugins;

import java.util.function.Predicate;

import com.pixbits.lib.plugin.Plugin;

import jack.rm.data.romset.RomSet;
import jack.rm.gui.PluginOptionsPanel;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public abstract class ActualPlugin extends Plugin
{
  protected Predicate<RomSet> compatibility() { return rs -> true; }
    
  protected void message(String message) { Log.message(LogSource.PLUGINS, LogTarget.plugin(this), message); }
  protected void warning(String message) { Log.warning(LogSource.PLUGINS, LogTarget.plugin(this), message); }
  protected void error(String message) { Log.error(LogSource.PLUGINS, LogTarget.plugin(this), message); }

  public PluginOptionsPanel getGUIPanel() { return null; }
}
