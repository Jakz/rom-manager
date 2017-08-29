package com.github.jakz.romlib.data.game.attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AttributeSet implements GameAttributeInterface
{
  private final Map<Attribute, Object> attributes = new HashMap<>();
  private final Map<Attribute, Object> customAttributes = new HashMap<>();
  
  public AttributeSet()
  {
    
  }
  
  public void setAttribute(Attribute key, Object value)
  { 
    attributes.put(key, value);
  }
  
  public void setCustomAttribute(Attribute key, Object value)
  { 
    customAttributes.put(key, value);
  }
  
  @SuppressWarnings("unchecked") public <T> T getAttribute(Attribute key)
  { 
    return (T)customAttributes.getOrDefault(key, attributes.get(key));
  }
  
  public boolean hasAttribute(Attribute key)
  {
    return customAttributes.containsKey(key) || attributes.containsKey(key);
  }
  
  public boolean hasAnyCustomAttribute()
  {
    return !customAttributes.isEmpty();
  }
  
  public <T> T computeIfAbsent(Attribute key, Supplier<T> supplier)
  {
    T attrib = getAttribute(key);
    
    if (attrib == null)
    {
      attrib = supplier.get();
      setAttribute(key, attrib);
    }
   
    return attrib;
  }
  
  public Stream<Map.Entry<Attribute, Object>> getCustomAttributes() { return customAttributes.entrySet().stream(); }
  public boolean hasCustomAttribute(Attribute attrib) { return customAttributes.containsKey(attrib); }
  public void clearCustomAttribute(Attribute attrib) { customAttributes.remove(attrib); }
  
}
