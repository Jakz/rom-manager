package jack.rm.plugins.datparsers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.pixbits.parser.SimpleParser;
import com.pixbits.parser.SimpleTreeBuilder;

import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomSize;
import jack.rm.data.romset.DatFormat;
import jack.rm.data.romset.RomSet;
import jack.rm.files.parser.DatLoader;

public class ClrMameParserPlugin extends DatParserPlugin
{
  @Override public String[] getSupportedFormats() { return new String[] {"clr-mame"}; }
  
  @Override
  public DatLoader buildDatLoader(String format, Map<String, Object> arguments)
  {
    if (format.equals("clr-mame"))
      return new ClrMameParser();
    else
      return null;
  }
  
  public class ClrMameParser implements DatLoader
  {
    RomSet set;
    Rom rom;

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
        rom.setAttribute(RomAttribute.TITLE, v);
      else if (k.equals("size"))
        rom.setSize(RomSize.forBytes(Long.parseLong(v)));
      else if (k.equals("crc"))
        rom.setAttribute(RomAttribute.CRC, Long.parseLong(v, 16));
    }
    
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
