package jack.rm.plugin;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

import jack.rm.plugins.PluginRealType;

import java.util.Map;

public class PluginManager
{
  private final Set<PluginBuilder> plugins;
  
  public PluginManager()
  {
    plugins = new HashSet<>();
  }
  
  public void clear()
  {
    plugins.clear();
  }
  
  public boolean register(PluginType type, Class<? extends Plugin> clazz)
  { 
    boolean alreadyRegistered = stream().anyMatch( p -> p.getPluginClass().equals(clazz));
    
    if (alreadyRegistered)
      return false; // TODO: throw exception: plugin already loaded, should add plugin version to comparison and discard less recent if found
    else
    {
      try
      {
        Plugin plugin = clazz.newInstance();
        PluginBuilder builder = new PluginBuilder(plugin);
        plugins.add(builder);
        return true;
      }
      catch (IllegalAccessException|InstantiationException e)
      {
        // TODO: unable to load plugin, no default public constructor
        e.printStackTrace();
        return false;
      }
    }
  }
  
  public Stream<PluginBuilder> stream() { return plugins.stream(); }
  
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
