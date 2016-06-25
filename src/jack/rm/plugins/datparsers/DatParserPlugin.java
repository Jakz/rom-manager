package jack.rm.plugins.datparsers;

import java.util.Map;
import java.util.function.Predicate;

import com.pixbits.plugin.PluginType;

import jack.rm.data.romset.RomSet;
import jack.rm.files.parser.DatLoader;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class DatParserPlugin extends ActualPlugin
{
  @Override public boolean isNative() { return true; }
  @Override public PluginType<?> getPluginType() { return PluginRealType.DAT_PARSER; }
  
  protected <T> void checkArgument(Map<String,Object> arguments, String key, Class<T> clazz)
  {
    if (!arguments.containsKey(key) || !clazz.isAssignableFrom(arguments.get(key).getClass()))
      throw new IllegalArgumentException("Parser "+getClass().getName()+" requires argument '"+key+"' of type "+clazz.getName());
  }
  
  public abstract String[] getSupportedFormats();
  public abstract DatLoader buildDatLoader(String format, Map<String,Object> arguments);
  public DatLoader buildDatLoader(String format) { return buildDatLoader(format, null); }
  
  protected Predicate<RomSet> compatibility() { return rs -> false; }
}
