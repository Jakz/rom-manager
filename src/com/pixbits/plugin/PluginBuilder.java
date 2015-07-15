package com.pixbits.plugin;

public class PluginBuilder<T extends Plugin>
{  
  private final PluginID id;
  
  public final PluginType type;
  public final PluginInfo info;
  
  public PluginBuilder(T dummy)
  {
    this.id = new PluginID((Class<? extends Plugin>)dummy.getClass());
    this.type = dummy.getPluginType();
    this.info = dummy.getInfo();
  }
  
  @SuppressWarnings("unchecked")
  @Override public boolean equals(Object object) { return object instanceof PluginBuilder && ((PluginBuilder<T>)object).id.equals(id); }
  @Override public int hashCode() { return id.hashCode(); }
  public PluginID getID() { return id; }
 }
