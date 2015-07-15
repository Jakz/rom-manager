package jack.rm.plugin;

import java.util.HashSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

import jack.rm.plugins.PluginRealType;

import java.util.Map;

public class PluginManager<T extends Plugin, B extends PluginBuilder<T>>
{
  private final Set<B> plugins;
  private final Class<B> builderClass;
  
  public PluginManager(Class<B> builderClass)
  {
    plugins = new HashSet<>();
    this.builderClass = builderClass;
  }
  
  public void clear()
  {
    plugins.clear();
  }
  
  public boolean register(Class<? extends T> clazz)
  { 
    boolean alreadyRegistered = stream().anyMatch( p -> p.getID().getType().equals(clazz));
    

    
    if (alreadyRegistered)
      return false; // TODO: throw exception: plugin already loaded, should add plugin version to comparison and discard less recent if found
    else
    {
      try
      {
        /* ugly hack to find correct constructor for builder since T is lost at runtime for type erasure */
        Constructor<B> constructor = null;
        Class<?> argumentClass = clazz;
        boolean found = false;
        while (!found)
        {
          try
          {
            constructor = builderClass.getConstructor(argumentClass);
            found = true;
          }
          catch (NoSuchMethodException e)
          {
            argumentClass = argumentClass.getSuperclass();
          }
        }

        Class<?> fatherClass = clazz;
        while (fatherClass.getSuperclass() != Plugin.class)
          fatherClass = fatherClass.getSuperclass();
          
        T plugin = clazz.newInstance();
        B builder = constructor.newInstance(plugin);
        
        plugins.add(builder);
        return true;
      }
      catch (IllegalAccessException|InstantiationException|InvocationTargetException e)
      {
        // TODO: unable to load plugin, no default public constructor
        e.printStackTrace();
        return false;
      }
    }
  }
  
  public Stream<B> stream() { return plugins.stream(); }
  
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
