package com.pixbits.plugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginSet<P extends Plugin>
{
  private final Set<P> plugins;
  
  public PluginSet()
  {
    plugins = new HashSet<>();
  }
  
  public void add(P plugin)
  {
    plugins.add(plugin);
  }
  
  @SuppressWarnings("unchecked")
  public void enable(PluginManager<P, ?> manager, PluginID id)
  {
    P plugin = getPlugin(id).orElse(manager.build((Class<? extends P>)id.getType()));
    
    if (plugin.isEnabled())
      return;
    
    /* if plugin is mutually exclusive we need to disable all others of same type */
    if (plugin.getPluginType().isMutuallyExclusive())
    {
      getEnabledPlugins(plugin.getPluginType()).stream()
      .filter( p -> p.isEnabled() )
      .forEach( p -> p.setEnabled(false) );
    }
    
    plugins.add(plugin);
    plugin.setEnabled(true);
  }
  
  public void disable(PluginID id)
  {
    Optional<P> plugin = getPlugin(id);
    
    if (plugin.isPresent())
      plugin.get().setEnabled(false);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> Set<T> getPlugins(PluginType<?> type)
  {
    return (Set<T>)(Set<?>)stream().filter( p -> p.getPluginType() == type).collect(Collectors.toSet()); 
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> Set<T> getEnabledPlugins(PluginType<?> type)
  {
    return (Set<T>)(Set<?>)getPlugins(type).stream().filter(p -> p.isEnabled()).collect(Collectors.toSet());
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> T getPlugin(PluginType<?> type)
  {
    return (T)stream().filter( p -> p.getPluginType() == type).findFirst().orElse(null);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> T getEnabledPlugin(PluginType<?> type)
  {
    return (T)stream().filter( p -> p.getPluginType() == type && p.isEnabled()).findFirst().orElse(null);
  }
  
  public boolean hasPlugin(PluginType<?> type)
  {
    return stream().anyMatch( p -> p.getPluginType() == type );
  }
  
  public boolean hasPlugin(PluginID id)
  {
    return getPlugin(id).isPresent();
  }
  
  public Optional<P> getPlugin(PluginID id)
  {
    return stream().filter( p -> p.getID().equals(id) ).findFirst();
  }
  
  public Stream<P> stream() { return plugins.stream(); }
}
