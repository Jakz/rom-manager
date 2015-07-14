package jack.rm.plugin;

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
  public <T extends Plugin> Set<T> getPlugins(PluginType type)
  {
    return (Set<T>)(Set<?>)stream().filter( p -> p.getType() == type).collect(Collectors.toSet()); 
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> Set<T> getEnabledPlugins(PluginType type)
  {
    return (Set<T>)(Set<?>)getPlugins(type).stream().filter(p -> p.isEnabled()).collect(Collectors.toSet());
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> T getPlugin(PluginType type)
  {
    return (T)stream().filter( p -> p.getType() == type).findFirst().get();
  }
  
  public boolean hasPlugin(PluginType type)
  {
    return stream().anyMatch( p -> p.getType() == type );
  }
  
  public boolean hasPlugin(PluginBuilder<P> builder)
  {
    return stream().anyMatch( p -> p.getClass().equals(builder.getPluginClass()));
  }
  
  public Optional<P> getPlugin(PluginBuilder<P> builder)
  {
    return stream().filter( p -> p.getClass().equals(builder.getPluginClass())).findFirst();
  }
  
  public Stream<P> stream() { return plugins.stream(); }
}
