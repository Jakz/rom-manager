package jack.rm.plugin;

import java.lang.reflect.Field;

public class PluginArgument
{
  private final Class<?> type;
  private final String name;
  private final Field field;
  private final Plugin plugin;
  
  PluginArgument(Plugin plugin, Field field, String name, Class<?> type)
  {
    this.plugin = plugin;
    this.field = field;
    this.name = name;
    this.type = type;
  }
  
  public Class<?> getType() { return type; }
  public String getName() { return name; }
  
  public Object get()
  {
    try
    {
      return field.get(plugin);
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  public void set(Object object)
  {
    try
    {
      field.set(plugin, object);
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
  }
}
