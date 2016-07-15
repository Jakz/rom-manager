package jack.rm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jack.rm.json.Json;

public class GlobalSettings
{  
  public static final Path DATA_PATH = Paths.get("data/");
  
  private final List<String> enabledProviders;
  private String currentProvider;
  private boolean alwaysScanWhenLoadingRomset;
  
  public GlobalSettings()
  {
    enabledProviders = new ArrayList<>();
    alwaysScanWhenLoadingRomset = false;
  }
  
  public void markCurrentProvider(String ident) { currentProvider = ident; }
  public String getCurrentProvider() { return currentProvider; }
  public List<String> getEnabledProviders() { return enabledProviders; }
  public void enableProvider(String ident) { enabledProviders.add(ident); }
  public void disableProvider(String ident) { enabledProviders.remove(ident); }
  
  public boolean shouldScanWhenLoadingRomset() { return alwaysScanWhenLoadingRomset; }
  
  public static GlobalSettings settings = new GlobalSettings();
  
  public static void save()
  {
    try
    {
      Path settingsPath = DATA_PATH.resolve("settings.json");
    
      try (BufferedWriter wrt = Files.newBufferedWriter(settingsPath))
      {
        wrt.write(Json.build().toJson(settings, GlobalSettings.class));
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void load()
  {
    try
    {
      Path settingsPath = DATA_PATH.resolve("settings.json");

      try (BufferedReader rdr = Files.newBufferedReader(settingsPath))
      {
        settings = Json.build().fromJson(rdr, GlobalSettings.class);
      }
    }
    catch (NoSuchFileException e)
    {
      
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
