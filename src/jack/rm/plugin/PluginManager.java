package jack.rm.plugin;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;

public class PluginManager
{
  private final Map<PluginType, Set<Class<? extends Plugin>>> plugins;
  
  public PluginManager()
  {
    plugins = new HashMap<>();
  }
  
  public void clear()
  {
    plugins.clear();
  }
  
  public boolean register(PluginType type, Class<? extends Plugin> plugin)
  { 
    Set<Class<? extends Plugin>> set = plugins.get(type);
    
    if (set != null && set.contains(plugin))
      return false; // TODO: throw exception: plugin already loaded
    else
    {
      try { plugin.getClass().getConstructor(); }
      catch (NoSuchMethodException e)
      {
        // plugin doesn't have a default constructor, state can't be unserialized
        return false;
      }
      
      if (set == null)
      {
        set = new HashSet<>();
        plugins.put(type, set);
      }

      set.add(plugin);
      return true;
    }
  }
  
  public Plugin build(Class<? extends Plugin> clazz)
  {
    try
    {   
      Plugin plugin = clazz.newInstance();
      return plugin;
    }
    catch (IllegalAccessException|InstantiationException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
    
  private static PluginManager instance = null;
  
  public static PluginManager getInstance() {
    return instance;
  }
  
  
  static
  {
    instance = new PluginManager();
    instance.register(PluginRealType.FOLDER_ORGANIZER, jack.rm.plugins.folder.NumericalOrganizer.class);
    instance.register(PluginRealType.ROMSET_CLEANUP, jack.rm.plugins.cleanup.DeleteEmptyFoldersPlugin.class);
  }
}
