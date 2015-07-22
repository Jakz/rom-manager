package jack.rm.plugins.renamer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import jack.rm.data.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;
import jack.rm.files.Pattern;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

public class NumberedRomPattern extends PatternSetPlugin
{
  private final DecimalFormat format;

  public NumberedRomPattern()
  {
    format = new DecimalFormat();
    format.applyPattern("0000");
  }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Numeric Pattern Set", new PluginVersion(1,0), "Jack",
        "This plugin provides a naming pattern for ROMs that have a number.");
  }

  private class NumberPattern extends Pattern
  {
    NumberPattern()
    { 
      super("%n", "Release number in format 1234");
    }
    
    @Override
    public String apply(String name, Rom rom)
    { 
      return name.replace(code,format.format((int)rom.getAttribute(RomAttribute.NUMBER)));
    }
  }
  
  private final Pattern[] patterns = { new NumberPattern() };
  
  @Override
  public List<Pattern> getPatterns()
  {
    return Arrays.asList(patterns);
  }
  
  @Override
  public Predicate<RomSet<?>> compatibility() { return rs -> rs.doesSupportAttribute(RomAttribute.NUMBER); }
}
