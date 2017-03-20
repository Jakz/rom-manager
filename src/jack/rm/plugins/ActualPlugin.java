package jack.rm.plugins;

import java.util.function.Predicate;

import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;
import com.pixbits.lib.plugin.Plugin;

import jack.rm.data.romset.GameSet;
import jack.rm.gui.PluginOptionsPanel;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;

public abstract class ActualPlugin extends Plugin
{
  private final static Logger logger = Log.getLogger(LogSource.PLUGINS);
  
  protected Predicate<GameSet> compatibility() { return rs -> true; }
    
  protected void message(String message) { logger.i(LogTarget.plugin(this), message); }
  protected void warning(String message) { logger.w(LogTarget.plugin(this), message); }
  protected void error(String message) { logger.e(LogTarget.plugin(this), message); }

  public PluginOptionsPanel getGUIPanel() { return null; }
}
