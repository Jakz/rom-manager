package com.pixbits.plugin;

import java.util.HashSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  
  @SuppressWarnings("unchecked")
  public void setup(PluginSet<T> set)
  {
    Map<PluginType<?>, Set<B>> map = plugins.stream().collect(Collectors.groupingBy(b -> b.type, HashMap::new, Collectors.toSet()));
    
    map.forEach( (k, v) -> {
      if (k.isRequired())
      {
        Optional<B> nativePlugin = v.stream().filter(p -> p.isNative).findFirst();
                
        // TODO: if existing set check if not already enabled for type if mutually exclusive
        
        T plugin = this.build((Class<? extends T>)nativePlugin.get().getID().getType()); 
        
        set.add(plugin);
        set.enable(this, plugin.getID());
      }
    });
  }
  
  public Set<B> getBuildersByType(PluginType<?> type)
  {
    return plugins.stream().filter( b -> b.type == type).collect(Collectors.toSet());
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
        plugin.setManager(this);
        B builder = constructor.newInstance(plugin);
        
        plugins.add(builder);
        return true;
      }
      catch (IllegalAccessException|InstantiationException|InvocationTargetException e)
      {
        throw new PluginException("unable to register plugin "+clazz.getName()+", does it have a public constructor?");
      }
    }
  }
  
  public Stream<B> stream() { return plugins.stream(); }
  
  public T build(Class<? extends T> clazz)
  {
    try
    {   
      T plugin = clazz.newInstance();
      plugin.setManager(this);
      return plugin;
    }
    catch (IllegalAccessException|InstantiationException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
}
