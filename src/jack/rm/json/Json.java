package jack.rm.json;

import java.nio.file.Path;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jack.rm.data.RomFileEntry;
import jack.rm.data.set.RomSet;
import jack.rm.plugin.Plugin;
import jack.rm.plugin.PluginSet;
import jack.rm.plugin.json.JsonPluginSetAdapter;
import jack.rm.plugins.ActualPlugin;

public class Json
{
  private static Map<Class<?>, Object> typeAdapters = new HashMap<>();
  
  public static void registerTypeAdapter(Class<?> clazz, Object adapter)
  {
    typeAdapters.put(clazz, adapter);
  }
  
  static
  {
    registerTypeAdapter(RomSet.class, new RomSetSerializer());
    registerTypeAdapter(RomFileEntry.class, new RomFileEntry.Adapter());
    registerTypeAdapter(Path.class, new PathSerializer());
    registerTypeAdapter(Plugin.class, new JsonPluginAdapter<Plugin>());
    registerTypeAdapter(PluginSet.class, new JsonPluginSetAdapter<ActualPlugin>());
  }
  
  public static Gson build()
  {
    GsonBuilder builder = new GsonBuilder();   
    typeAdapters.forEach( (k,v) -> builder.registerTypeHierarchyAdapter(k, v) );
    return builder.setPrettyPrinting().create();
  }
}
