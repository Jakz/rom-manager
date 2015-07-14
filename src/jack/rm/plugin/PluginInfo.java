package jack.rm.plugin;

public class PluginInfo
{
  //private final Class<? extends Plugin> clazz;
  
  public final String name;
  public final PluginVersion version;
  
  public final String author;
  public final String description;
  
  public PluginInfo(String name, PluginVersion version, String author, String description)
  {
    this.name = name;
    this.version = version;
    this.author = author;
    this.description = description;
  }
  
  public String getSimpleName() { return name + " " + version; }
  
}
