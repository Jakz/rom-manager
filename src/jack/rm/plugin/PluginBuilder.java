package jack.rm.plugin;

public class PluginBuilder
{
  private final Class<? extends Plugin> clazz;
  
  public final PluginType type;
  public final String name;
  public final String author;
  public final String description;
  public final PluginVersion version;
  
  PluginBuilder(Plugin dummy)
  {
    this.clazz = dummy.getClass();
    this.type = dummy.getType();
    this.name = dummy.getName();
    this.author = dummy.getAuthor();
    this.description = dummy.getDescription();
    this.version = dummy.getVersion();
  }
  
  Class<? extends Plugin> getPluginClass() { return clazz; }
 }
