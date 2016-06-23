package com.pixbits.plugin;

public class PluginException extends RuntimeException
{
  PluginException(String message)
  {
    super("Plugin exception: "+message);
  }
}
