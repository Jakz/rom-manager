package jack.rm.plugins.providers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.pixbits.plugin.PluginType;

import jack.rm.data.console.System;
import jack.rm.data.romset.RomSet;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.datparsers.DatParserPlugin;

public abstract class ProviderPlugin extends ActualPlugin
{
  public abstract RomSet[] buildRomSets(List<DatParserPlugin> datParsers);
  //public abstract boolean isSystemSupported(System system);
  
  protected DatParserPlugin findDatParser(List<DatParserPlugin> plugins, String type)
  {
    Optional<DatParserPlugin> plugin = plugins.stream().filter(p -> Arrays.asList(p.getSupportedFormats()).contains(type)).findFirst();
    
    if (!plugin.isPresent())
      throw new UnsupportedOperationException("ProviderPlugin "+this.getClass().getName()+" requires a dat parser for "+type+" format");
    
    return plugin.isPresent() ? plugin.get() : null;
  }
  
  public PluginType<?> getPluginType() { return PluginRealType.PROVIDER; }
  
  protected Predicate<RomSet> compatibility() { return rs -> false; }

}
