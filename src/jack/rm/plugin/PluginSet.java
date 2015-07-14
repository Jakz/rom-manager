package jack.rm.plugin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginSet
{
  private final Set<Plugin> plugins;
  
  public PluginSet()
  {
    plugins = new HashSet<>();
    
    add(PluginManager.getInstance().build(jack.rm.plugins.folder.NumericalOrganizer.class));
    add(PluginManager.getInstance().build(jack.rm.plugins.cleanup.DeleteEmptyFoldersPlugin.class));
  }
  
  public void add(Plugin plugin)
  {
    plugins.add(plugin);
  }
  
  @SuppressWarnings("unchecked")
  public <T extends Plugin> Set<T> getPlugins(PluginType type)
  {
    return (Set<T>)(Set<?>)stream().filter( p -> p.getType() == type).collect(Collectors.toSet()); 
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
  
  public boolean hasPlugin(PluginBuilder builder)
  {
    return stream().anyMatch( p -> p.getClass().equals(builder.getPluginClass()));
  }
  
  public Stream<Plugin> stream() { return plugins.stream(); }
}
