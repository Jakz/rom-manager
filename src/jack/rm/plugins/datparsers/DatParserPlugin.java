package jack.rm.plugins.datparsers;

import java.util.Map;
import java.util.function.Predicate;

import com.github.jakz.romlib.data.set.DatLoader;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.data.romset.GameSet;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

/**
 * This class provides the basic interface for a plugin that produces one or more @{link DatLoader} which is the object responsible
 * for parsing a DAT file.
 * 
 * If you want to support a specific DAT format, you need to subclass this plugin to make the rom manager be able to build the <code>DatLoader</code>.
 * A <code>DatLoader</code> is identified univocally by a <code>String</code>. In addition it's possible to pass to the method which builds the parser a
 * map of generic <code>key, value</code> pairs which is used by @{link ProviderPlugin} to specify parameters for the parser.
 * 
 * @author Jack
 *
 */
public abstract class DatParserPlugin extends ActualPlugin
{
  @Override public final boolean isNative() { return true; }
  @Override public final PluginType<?> getPluginType() { return PluginRealType.DAT_PARSER; }
  
  protected <T> void checkArgument(Map<String,Object> arguments, String key, Class<T> clazz)
  {
    if (!arguments.containsKey(key) || !clazz.isAssignableFrom(arguments.get(key).getClass()))
      throw new IllegalArgumentException("Parser "+getClass().getName()+" requires argument '"+key+"' of type "+clazz.getName());
  }
  
  /** @return an array of <code>String</code> which are the unique identifiers for the @{link DatLoader} that this plugin is able to generate */
  public abstract String[] getSupportedFormats();
  /**
   * This method builds the actual <code>DatLoader</code> and return it
   * @param format the unique identifier of the requested parser
   * @param arguments the map of arguments passed to the parser when instantiating it
   * @return
   */
  public abstract DatLoader buildDatLoader(String format, Map<String,Object> arguments);
  public DatLoader buildDatLoader(String format) { return buildDatLoader(format, null); }
  
  protected final Predicate<GameSet> compatibility() { return rs -> false; }
}
