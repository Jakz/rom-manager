package jack.rm.plugins.providers.clrmame;

import java.io.FileInputStream;
import com.pixbits.parser.SimpleParser;
import com.pixbits.parser.SimpleTreeBuilder;

import jack.rm.data.*;
import jack.rm.data.parser.DatLoader;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;

public class ClrMameParser implements DatLoader
{
	RomSet set;
  Rom rom;

  public void load(RomSet set)
  {
    this.set = set;
    load(set.datPath());
  }
  
  
	
	public void load(String datFile)
	{
		try
		{
			FileInputStream fis = new FileInputStream(datFile);

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
