package jack.rm.json;

import java.nio.file.Path;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixbits.plugin.JsonPluginSetAdapter;
import com.pixbits.plugin.Plugin;
import com.pixbits.plugin.PluginSet;

import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.RomID;
import jack.rm.data.rom.RomPath;
import jack.rm.data.romset.RomSet;
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
    registerTypeAdapter(RomSavedAttribute.class, new RomSavedAttributeAdapter());
    registerTypeAdapter(Path.class, new PathAdapter());
    registerTypeAdapter(Plugin.class, new JsonPluginAdapter<Plugin>());
    registerTypeAdapter(PluginSet.class, new JsonPluginSetAdapter<ActualPlugin>());
    registerTypeAdapter(Attribute.class, new RomAttributeAdapter());

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
