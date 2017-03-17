package jack.rm.json;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.JsonHandleAdapter;
import com.pixbits.lib.json.PathAdapter;
import com.pixbits.lib.plugin.JsonPluginSetAdapter;
import com.pixbits.lib.plugin.Plugin;
import com.pixbits.lib.plugin.PluginSet;

import jack.rm.data.attachment.Attachment;
import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.RomID;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.ActualPlugin;

public class Json
{
  private static Map<Class<?>, Object> typeAdapters = new HashMap<>();
  private static Map<Class<?>, Object> hierarchyAdapters = new HashMap<>();

  
  public static void registerTypeAdapter(Class<?> clazz, Object adapter)
  {
    typeAdapters.put(clazz, adapter);
  }
  
  public static void registerHiearchyAdapter(Class<?> clazz, Object adapter)
  {
    hierarchyAdapters.put(clazz, adapter);
  }
  
  static
  {
    registerTypeAdapter(RomSet.class, new RomSetAdapter());
    registerTypeAdapter(RomID.class, new RomIdAdapter());
    registerHiearchyAdapter(Handle.class, new JsonHandleAdapter());
    registerTypeAdapter(RomSavedAttribute.class, new RomSavedAttributeAdapter());
    registerHiearchyAdapter(Path.class, new PathAdapter());
    registerTypeAdapter(Plugin.class, new JsonPluginAdapter<Plugin>());
    registerTypeAdapter(PluginSet.class, new JsonPluginSetAdapter<ActualPlugin>());
    registerTypeAdapter(Attribute.class, new RomAttributeAdapter());
    registerTypeAdapter(Attachment.class, new AttachmentAdapter());

  }
  
  public static Gson build()
  {
    return prebuild().create();
  }
  
  public static GsonBuilder prebuild()
  {
    GsonBuilder builder = new GsonBuilder();   
    typeAdapters.forEach( (k,v) -> builder.registerTypeHierarchyAdapter(k, v) );
    hierarchyAdapters.forEach( (k, v) -> builder.registerTypeHierarchyAdapter(k, v) );
    return builder.setPrettyPrinting();
  }
}
