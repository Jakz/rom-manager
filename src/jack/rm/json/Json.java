package jack.rm.json;

import java.nio.file.Path;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixbits.plugin.Plugin;
import com.pixbits.plugin.PluginSet;

import jack.rm.data.RomPath;
import jack.rm.data.RomID;
import jack.rm.data.set.RomSet;
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
    registerTypeAdapter(RomSet.class, new RomSetAdapter());
    registerTypeAdapter(RomID.class, new RomIdAdapter());
    registerTypeAdapter(RomPath.class, new RomPathAdapter());
    registerTypeAdapter(Path.class, new PathAdapter());
    registerTypeAdapter(Plugin.class, new JsonPluginAdapter<Plugin>());
    registerTypeAdapter(PluginSet.class, new JsonPluginSetAdapter<ActualPlugin>());
  }
  
  public static Gson build()
  {
    return prebuild().create();
  }
  
  public static GsonBuilder prebuild()
  {
    GsonBuilder builder = new GsonBuilder();   
    typeAdapters.forEach( (k,v) -> builder.registerTypeHierarchyAdapter(k, v) );
    return builder.setPrettyPrinting();
  }
}
