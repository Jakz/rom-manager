package jack.rm.plugin;

public class PluginBuilder<T extends Plugin>
{
  private final Class<? extends T> clazz;
  
  public final PluginType type;
  public final PluginInfo info;
  
  @SuppressWarnings("unchecked")
  PluginBuilder(T dummy)
  {
    this.clazz = (Class<? extends T>) dummy.getClass();
    this.type = dummy.getType();
    this.info = dummy.getInfo();
  }
  
  Class<? extends Plugin> getPluginClass() { return clazz; }
 }
