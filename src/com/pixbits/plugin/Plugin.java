package com.pixbits.plugin;

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
import com.pixbits.json.JsonnableContext;
import com.pixbits.stream.StreamException;

public abstract class Plugin implements JsonnableContext
{
  private final PluginID id;
  boolean enabled;
  
  public Plugin()
  {
    id = new PluginID(this);
    enabled = false;
  }
  
  public boolean isEnabled() { return enabled; }
  public void setEnabled(boolean enabled) { this.enabled = enabled; }
  
  @Override public boolean equals(Object object) { return object instanceof Plugin && ((Plugin)object).id.equals(id); }
  @Override public int hashCode() { return id.hashCode(); }
  public PluginID getID() { return id; }

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
      String description = !annotation.description().isEmpty() ? annotation.description() : null;  
      return new PluginArgument(this, f, name, f.getType(), description);
    }).collect(Collectors.toList());
  }

  public PluginInfo getInfo()
  { 
    return new PluginInfo(getClass().getSimpleName(), new PluginVersion(1,0), "", "None");
  }
 
  public abstract PluginType getPluginType();
}
