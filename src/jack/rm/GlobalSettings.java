package jack.rm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jack.rm.data.console.System;

public class GlobalSettings
{  
  private final Set<String> enabledProviders;
  private String currentProvider;
  
  public GlobalSettings()
  {
    enabledProviders = new HashSet<>();
  }
  
  public String getCurrentProvider() { return currentProvider; }
  public Set<String> getEnabledProviders() { return enabledProviders; }
  public void enableProvider(String ident) { enabledProviders.add(ident); }
  public void disableProvider(String ident) { enabledProviders.remove(ident); }
  
  public static GlobalSettings settings = new GlobalSettings();
  
  public static void save()
  {
    
  }
}
