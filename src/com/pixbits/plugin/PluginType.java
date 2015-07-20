package com.pixbits.plugin;

public interface PluginType<T extends PluginType<T>> extends Comparable<T>
{
  public boolean isMutuallyExclusive();
  public boolean isRequired();
}
