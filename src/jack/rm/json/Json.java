package jack.rm.json;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.github.jakz.romlib.data.attachments.Attachment;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.game.attributes.Attribute;
import com.github.jakz.romlib.json.AttachmentAdapter;
import com.github.jakz.romlib.json.GameAttributeAdapter;
import com.github.jakz.romlib.json.GameIdAdapter;
import com.github.jakz.romlib.json.GameSavedAttribute;
import com.github.jakz.romlib.json.GameSavedAttributeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.JsonHandleAdapter;
import com.pixbits.lib.json.PathAdapter;
import com.pixbits.lib.plugin.JsonPluginSetAdapter;
import com.pixbits.lib.plugin.Plugin;
import com.pixbits.lib.plugin.PluginSet;

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
    registerTypeAdapter(GameID.class, new GameIdAdapter());
    registerHiearchyAdapter(Handle.class, new JsonHandleAdapter());
    registerTypeAdapter(GameSavedAttribute.class, new GameSavedAttributeAdapter());
    registerHiearchyAdapter(Path.class, new PathAdapter());
    registerTypeAdapter(Plugin.class, new JsonPluginAdapter<Plugin>());
    registerTypeAdapter(PluginSet.class, new JsonPluginSetAdapter<ActualPlugin>());
    registerTypeAdapter(Attribute.class, new GameAttributeAdapter());
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
