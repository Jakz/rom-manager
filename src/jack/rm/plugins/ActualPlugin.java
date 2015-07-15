package jack.rm.plugins;

import java.util.function.Predicate;

import jack.rm.data.set.RomSet;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugin.Plugin;
import jack.rm.plugin.PluginManager;

public abstract class ActualPlugin extends Plugin
{
  public static final PluginManager<ActualPlugin, ActualPluginBuilder> manager = new PluginManager<>(ActualPluginBuilder.class);
  
  static
  {
    manager.register(jack.rm.plugins.folder.NumericalOrganizer.class);
    manager.register(jack.rm.plugins.folder.AlphabeticalOrganizer.class);
    manager.register(jack.rm.plugins.cleanup.DeleteEmptyFoldersPlugin.class);
    manager.register(jack.rm.plugins.cleanup.MoveUnknownFilesPlugin.class);
  }
  
  protected Predicate<RomSet<?>> compatibility() { return rs -> true; }
    
  protected void message(String message) { Log.message(LogSource.PLUGINS, LogTarget.plugin(this), message); }
  protected void warning(String message) { Log.warning(LogSource.PLUGINS, LogTarget.plugin(this), message); }
  protected void error(String message) { Log.error(LogSource.PLUGINS, LogTarget.plugin(this), message); }

}
