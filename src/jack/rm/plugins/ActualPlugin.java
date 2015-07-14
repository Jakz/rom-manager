package jack.rm.plugins;

import jack.rm.plugin.Plugin;
import jack.rm.plugin.PluginManager;

public abstract class ActualPlugin extends Plugin
{
  public static final PluginManager<ActualPlugin> manager = new PluginManager<ActualPlugin>();
  
  static
  {
    manager.register(PluginRealType.FOLDER_ORGANIZER, jack.rm.plugins.folder.NumericalOrganizer.class);
    manager.register(PluginRealType.ROMSET_CLEANUP, jack.rm.plugins.cleanup.DeleteEmptyFoldersPlugin.class);
    manager.register(PluginRealType.ROMSET_CLEANUP, jack.rm.plugins.cleanup.MoveUnknownFilesPlugin.class);
  }
}
