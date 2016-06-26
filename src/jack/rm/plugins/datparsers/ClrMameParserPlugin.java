package jack.rm.plugins.datparsers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.pixbits.parser.SimpleParser;
import com.pixbits.parser.SimpleTreeBuilder;

import jack.rm.data.rom.Language;
import jack.rm.data.rom.Location;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomSize;
import jack.rm.data.romset.DatFormat;
import jack.rm.data.romset.RomSet;
import jack.rm.files.parser.DatLoader;

public class ClrMameParserPlugin extends DatParserPlugin
{
  @Override public String[] getSupportedFormats() { return new String[] {"clr-mame", "clr-mame-nointro"}; }
  
  @Override
  public DatLoader buildDatLoader(String format, Map<String, Object> arguments)
  {
    if (format.equals("clr-mame"))
      return new ClrMameParser();
    else if (format.equals("clr-mame-nointro"))
      return new ClrMameNoIntroParser();
    else
      return null;
  }
  
  private class ClrMameNoIntroParser extends ClrMameParser
  {
    Set<String> addendums = new HashSet<String>();
    
    @Override protected void parseRomTitle(String title)
    {
      int firstParen = title.indexOf('(');
      
      AtomicBoolean usa = new AtomicBoolean(false);
      AtomicBoolean japan = new AtomicBoolean(false);
      AtomicBoolean europe = new AtomicBoolean(false);
            
      Arrays.stream(title.substring(firstParen).split("\\(|\\)")).filter(s -> !s.isEmpty()).map(s -> s.trim()).forEach(s -> {
        Arrays.stream(s.split(",")).map(t -> t.trim()).filter(t -> !t.isEmpty()).forEach(t -> {
          if (t.equals("USA")) usa.set(true);
          else if (t.equals("Japan")) japan.set(true);
          else if (t.equals("Europe")) europe.set(true);
          else if (t.equals("Korea")) rom.setAttribute(RomAttribute.LOCATION, Location.KOREA);
          else if (t.equals("World")) rom.setAttribute(RomAttribute.LOCATION, Location.WORLD);
          else if (t.equals("Ja")) rom.getLanguages().add(Language.JAPANESE);
          else if (t.equals("Nl")) rom.getLanguages().add(Language.DUTCH);
          else if (t.equals("De")) rom.getLanguages().add(Language.GERMAN);
          else if (t.equals("No")) rom.getLanguages().add(Language.NORWEGIAN);
          else if (t.equals("Sv")) rom.getLanguages().add(Language.SWEDISH);
          else if (t.equals("Pt")) rom.getLanguages().add(Language.PORTUGUESE);
          else if (t.equals("En")) rom.getLanguages().add(Language.ENGLISH);
          else if (t.equals("It")) rom.getLanguages().add(Language.ITALIAN);
          else if (t.equals("Es")) rom.getLanguages().add(Language.SPANISH);
          else if (t.equals("Fr")) rom.getLanguages().add(Language.FRENCH);
          else if (t.equals("Proto") || t.equals("Proto 1") || t.equals("Development Edition") || 
                  t.equals("Rev 1") || t.equals("Proto 2") || t.equals("v2.0") || t.equals("Auto Demo") || t.equals("Sample") || t.equals("Beta"))
            rom.setAttribute(RomAttribute.VERSION, t);
          else 
          {
            rom.setAttribute(RomAttribute.COMMENT, t);
            addendums.add(t);
          }
          
        });
        
          
          
          /*String previous = rom.getAttribute(RomAttribute.COMMENT);
          if (previous == null) previous = "";
          rom.setAttribute(RomAttribute.COMMENT, previous + ", " + s);*/
      });
      
      rom.setAttribute(RomAttribute.TITLE, title.substring(0, firstParen-1));
      
      if (usa.get() && japan.get() && !europe.get())
        rom.setAttribute(RomAttribute.LOCATION, Location.USA_JAPAN);
      else if (usa.get() && !japan.get() && europe.get())
        rom.setAttribute(RomAttribute.LOCATION, Location.USA_EUROPE);
      else if (!usa.get() && japan.get() && europe.get())
        rom.setAttribute(RomAttribute.LOCATION, Location.JAPAN_EUROPE);
      else if (usa.get() && !japan.get() && !europe.get())
        rom.setAttribute(RomAttribute.LOCATION, Location.USA);
      else if (!usa.get() && japan.get() && !europe.get())
        rom.setAttribute(RomAttribute.LOCATION, Location.JAPAN);
      else if (!usa.get() && !japan.get() && europe.get())
        rom.setAttribute(RomAttribute.LOCATION, Location.EUROPE);

      
      if (rom.getAttribute(RomAttribute.LOCATION) == null)
        rom.setAttribute(RomAttribute.LOCATION, Location.NONE);
    }
    
    @Override protected void parsingFinished()
    {
      for (String s : addendums)
      {
        System.out.println(s);
      }
    }
  }
  
  private class ClrMameParser implements DatLoader
  {
    RomSet set;
    Rom rom;

    private final HexBinaryAdapter hexConverter = new HexBinaryAdapter();
    
    @Override public DatFormat getFormat() { return new DatFormat("cm", "dat"); }

    @Override public void load(RomSet set)
    {
      this.set = set;
      load(set.datPath());
    }
    
    public void load(Path datFile)
    {
      try (InputStream fis = Files.newInputStream(datFile))
      {

        //StringReader sr = new StringReader(input);  new ByteArrayInputStream(input.getBytes("UTF-8"))
        SimpleParser parser = new SimpleParser(fis);
        parser.addSingle('(', ')').addQuote('\"').addWhiteSpace(' ', '\t', '\r', '\n');

        SimpleTreeBuilder builder = new SimpleTreeBuilder(parser, this::pair, this::scope);
        builder.setScope("(", ")");
        
        parser.parse();
        
        parsingFinished();

      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    boolean started = false;
    boolean insideRom = false;

    public void pair(String k, String v)
    {
      if (!started)
        return;
      
      if (k.equals("name") && !insideRom)
        parseRomTitle(v);
      else if (k.equals("size"))
        rom.setSize(RomSize.forBytes(Long.parseLong(v)));
      else if (k.equals("crc"))
        rom.setAttribute(RomAttribute.CRC, Long.parseLong(v, 16));
      else if (k.equals("sha1"))
        rom.setAttribute(RomAttribute.MD5, hexConverter.unmarshal(v));
      else if (k.equals("md5"))
        rom.setAttribute(RomAttribute.SHA1, hexConverter.unmarshal(v));
    }
    
    protected void parseRomTitle(String title)
    {
      rom.setAttribute(RomAttribute.TITLE, title);
    }
    
    protected void parsingFinished() { }
    
    public void scope(String k, boolean isEnd)
    {   
      if (k.equals("rom"))
        insideRom = !isEnd;
      
      if (!isEnd && k.equals("game"))
        rom = new Rom(set);
      else if (isEnd && k.equals("game"))
        set.list.add(rom);
      else if (isEnd && k.equals("clrmamepro"))
        started = true;
    }
  }

}
