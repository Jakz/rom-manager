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
    return (Set<T>)(Set<?>)stream().filter( p -> p.getPluginType() == type).collect(Collectors.toSet()); 
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> Set<T> getEnabledPlugins(PluginType type)
  {
    return (Set<T>)(Set<?>)getPlugins(type).stream().filter(p -> p.isEnabled()).collect(Collectors.toSet());
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> T getPlugin(PluginType type)
  {
    return (T)stream().filter( p -> p.getPluginType() == type).findFirst().get();
  }
  
  public boolean hasPlugin(PluginType type)
  {
    return stream().anyMatch( p -> p.getPluginType() == type );
  }
  
  public boolean hasPlugin(PluginBuilder<?> builder)
  {
    return getPlugin(builder).isPresent();
  }
  
  public Optional<P> getPlugin(PluginBuilder<?> builder)
  {
    return stream().filter( p -> p.getID().equals(builder.getID()) ).findFirst();
  }
  
  public Stream<P> stream() { return plugins.stream(); }
}
