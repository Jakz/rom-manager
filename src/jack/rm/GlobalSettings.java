package jack.rm;

import java.util.HashMap;
import java.util.Map;

import jack.rm.data.console.System;

public class GlobalSettings
{  
  private final Map<System, String> defaultProviders;
  private String currentProvider;
  
  public GlobalSettings()
  {
    defaultProviders = new HashMap<>();
  }
  
  public String getCurrentProvider() { return currentProvider; }
  public String defaultProviderForSystem(System system) { return defaultProviders.get(system); }
  
  public static GlobalSettings settings = new GlobalSettings();
  
  public static void save()
  {
    
  }
}
