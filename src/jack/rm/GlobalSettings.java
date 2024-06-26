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
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.set.GameSetUUID;

import jack.rm.data.romset.GameSetManager;
import jack.rm.json.Json;

public class GlobalSettings
{  
  public static final Path DATA_PATH = Paths.get("data/");
  public static final Path DAT_PATH = Paths.get("dat/");
  
  private List<GameSetUUID> enabledProviders;
  private String currentProvider;
  private boolean alwaysScanWhenLoadingRomset;
  
  public GlobalSettings()
  {
    enabledProviders = new ArrayList<>();
    alwaysScanWhenLoadingRomset = false;
  }
  
  public void markCurrentProvider(String ident) { currentProvider = ident; }
  public String getCurrentProvider() { return currentProvider; }
  public List<GameSetUUID> getEnabledProviders() { return enabledProviders; }
  public void enableProvider(GameSetUUID ident) { enabledProviders.add(ident); }
  public void disableProvider(GameSetUUID ident) { enabledProviders.remove(ident); }
  
  public boolean shouldScanWhenLoadingRomset() { return alwaysScanWhenLoadingRomset; }
  
  /**
   * Removes sets from enabled sets or current selected which are 
   * not available anymore to the <code>GameSetManager</code>.
   * @param manager
   */
  public void sanitize(GameSetManager manager)
  {
    enabledProviders = enabledProviders.stream()
      .filter(i -> manager.byUUID(i) != null)
      .collect(Collectors.toList());
    
    currentProvider = manager.byIdent(currentProvider) != null ? currentProvider : null;
  }
  
  public static GlobalSettings settings = new GlobalSettings();
  
  public static void save()
  {
    try
    {
      Files.createDirectories(DATA_PATH);
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
