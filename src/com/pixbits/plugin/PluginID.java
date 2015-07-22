package com.pixbits.plugin;

public class PluginID
{
  private Class<? extends Plugin> clazz;
  
  PluginID(Class<? extends Plugin> clazz)
  {
    this.clazz = clazz;
  }
  
  PluginID(Plugin parent)
  {
    this.clazz = parent.getClass();
  }
  
  public boolean equals(Object other)
  {
    return other instanceof PluginID && ((PluginID)other).getType().equals(getType());
  }
  
  public int hashCode()
  {
    return clazz.hashCode();
  }
    
  public Class<? extends Plugin> getType()
  {
    return clazz;
  }
}
