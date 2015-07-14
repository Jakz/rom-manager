package jack.rm.plugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import jack.rm.StreamException;
import jack.rm.json.JsonnableContext;

public abstract class Plugin implements JsonnableContext
{
  boolean enabled;
  
  public Plugin()
  {
    enabled = true;
  }
  
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
  
  @Override
  public JsonElement serialize(JsonSerializationContext context) throws IllegalAccessException
  {
    JsonObject json = new JsonObject();
    json.add("class", new JsonPrimitive(this.getClass().getName()));
    json.add("isEnabled", new JsonPrimitive(enabled));

    getFields().stream()
    .forEach(StreamException.rethrowConsumer(f -> json.add(f.getName(), context.serialize(f.get(this))) ) );    
 
    return json;
  }
  
  @Override
  public void unserialize(JsonElement element, JsonDeserializationContext context) throws IllegalAccessException
  {     
    enabled = context.deserialize(element.getAsJsonObject().get("isEnabled"), Boolean.class);
    
    getFields().stream()
    .forEach(StreamException.rethrowConsumer(f -> f.set(this, context.deserialize(element.getAsJsonObject().get(f.getName()), f.getType()))) );    
  }
  
  private List<Field> getFields()
  {
    Class<?> clazz = this.getClass();
    List<Field> fields = new ArrayList<>();
        
    while (!clazz.equals(Plugin.class))
    {
      Arrays.stream(clazz.getDeclaredFields())
      .filter( f -> f.getAnnotation(ExposedParameter.class) != null)
      .map( f -> { f.setAccessible(true); return f; })
      .forEach(fields::add);
      
      clazz = clazz.getSuperclass();
    }
    
    return fields;
  }
  
  public List<PluginArgument> getArguments()
  {
    return getFields().stream().map( f -> {
      ExposedParameter annotation = f.getAnnotation(ExposedParameter.class);
      String name = !annotation.name().isEmpty() ? annotation.name() : f.getName();  
      return new PluginArgument(this, f, name, f.getType());
    }).collect(Collectors.toList());
  }

  @Override public int hashCode() { return this.getClass().hashCode(); }
  @Override public boolean equals(Object other) { return this.getClass().equals(other.getClass()); }

  public String getPrettyName() { return getName() + " " + getVersion(); }
  public String getName() { return getClass().getSimpleName(); }
  public String getAuthor() { return "Author"; }
  public String getDescription() { return "Description"; }
  public PluginVersion getVersion() { return new PluginVersion(1,0); }
  public abstract PluginType getType();
}
