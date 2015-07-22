package jack.rm.plugins.renamer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import jack.rm.data.Location;
import jack.rm.data.Language;
import jack.rm.data.Rom;
import jack.rm.data.RomSize;
import jack.rm.data.rom.RomAttribute;
import jack.rm.files.Pattern;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

public class BasicPatternSet extends PatternSetPlugin
{
  public BasicPatternSet()
  {

  }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Basic Pattern Set", new PluginVersion(1,0), "Jack",
        "This plugin provides the basic renaming patterns for ROMs.");
  }
  
  private static class AttributePattern extends Pattern 
  {
    RomAttribute attribute;
    AttributePattern(String code, String desc, RomAttribute attrib)
    {
      super(code, desc);
      this.attribute = attrib;
    }
    
    @Override public String apply(String name, Rom rom)
    { 
      return name.replace(code, attribute.prettyValue(rom.getAttribute(attribute)));
    }
  }
  
  private static class MegabyteSizePattern extends Pattern {
    MegabyteSizePattern() { super("%s", "Size of the game dump in bytes (long)"); }
    @Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.getSize().toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BYTES)); }
  }
  
  private static class MegabitSizePattern extends Pattern {
    MegabitSizePattern() { super("%S", "Size of the game dump in bits (short)"); }
    @Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.getSize().toString(RomSize.PrintStyle.SHORT, RomSize.PrintUnit.BITS)); }
  }
  
  private static class FullLocationPattern extends Pattern {
    FullLocationPattern() { super("%L", "Full location name"); }
    @Override
    public String apply(String name, Rom rom) { return name.replace(code,((Location)rom.getAttribute(RomAttribute.LOCATION)).fullName); }
  }
  
  private static class ShortLocationPattern extends Pattern {
    ShortLocationPattern() { super("%a", "Short location name"); }
    @Override
    public String apply(String name, Rom rom) { return name.replace(code,((Location)rom.getAttribute(RomAttribute.LOCATION)).shortName); }
  }
  
  private static class TinyLocationPattern extends Pattern {
    TinyLocationPattern() { super("%l", "Tiny location name"); }
    @Override
    public String apply(String name, Rom rom) { return name.replace(code,((Location)rom.getAttribute(RomAttribute.LOCATION)).tinyName); }
  }
  
  private static class ShortLanguagePattern extends Pattern {
    ShortLanguagePattern() { super("%i", "Short language"); }
    @Override
    public String apply(String name, Rom rom) {
      Stream<Language> stream = rom.getLanguages().stream();
      long langCount = rom.getLanguages().size();

      if (langCount == 1)
        return name.replace(code,stream.findFirst().get().iso639_1);
      else 
        return name.replace(code,"M"+langCount);
    }
  }
  
  private final Pattern[] patterns = {
    new FullLocationPattern(),
    new AttributePattern("%g", "Releaser group", RomAttribute.GROUP),
    new MegabitSizePattern(),
    new MegabyteSizePattern(),
    new AttributePattern("%c", "Publisher", RomAttribute.PUBLISHER),
    new ShortLanguagePattern(),
    new ShortLocationPattern(),
    new TinyLocationPattern(),
    new AttributePattern("%t", "Game title", RomAttribute.TITLE)
  };
  
  @Override
  public List<Pattern> getPatterns()
  {
    return Arrays.asList(patterns);
  }
}
