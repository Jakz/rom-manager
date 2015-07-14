package jack.rm.plugin;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

import jack.rm.plugins.PluginRealType;

import java.util.Map;

public class PluginManager<T extends Plugin>
{
  private final Set<PluginBuilder<T>> plugins;
  
  public PluginManager()
  {
    plugins = new HashSet<>();
  }
  
  public void clear()
  {
    plugins.clear();
  }
  
  public boolean register(PluginType type, Class<? extends T> clazz)
  { 
    boolean alreadyRegistered = stream().anyMatch( p -> p.getPluginClass().equals(clazz));
    
    if (alreadyRegistered)
      return false; // TODO: throw exception: plugin already loaded, should add plugin version to comparison and discard less recent if found
    else
    {
      try
      {
        T plugin = clazz.newInstance();
        PluginBuilder<T> builder = new PluginBuilder<T>(plugin);
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
  
  public Stream<PluginBuilder<T>> stream() { return plugins.stream(); }
  
  public T build(Class<? extends T> clazz)
  {
    try
    {   
      T plugin = clazz.newInstance();
      return plugin;
    }
    catch (IllegalAccessException|InstantiationException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
}
