package jack.rm.data.set;

import jack.rm.data.NumberedRom;
import jack.rm.data.Rom;
import jack.rm.data.RomSave;
import jack.rm.data.RomSize;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

public class GBA extends RomSetOfflineList implements NumberedSet<NumberedRom>
{
	public static class Save implements RomSave<Save.Type>
	{
	  public static enum Type implements RomSave.Type
	  {
	    EEPROM,
	    FLASH,
	    SRAM,
	    NONE
	  };
	  
	  private final Type type;
	  private long size;
	  private int version;
	  
	  public Save(Type type)
	  {
	    this.type = type;
	  }
	  
	  public Save(Type type, int version)
	  {
	    this(type);
	    this.version = version;
	  }
	  
	  public Save(Type type, int version, int size)
	  {
	    this(type, version);
	    this.size = size;
	  }
	  
	  public String toString()
	  {
	    String value = type.toString();
	    
	    if (version != 0)
	      value += " v"+version;
	    
	    if (size != 0)
	      value += " ("+RomSize.forBytes(size, false)+")";
	    
	    return value;
	  }
	  
	  public Type getType() { return type; }
	}
  
  public GBA() throws MalformedURLException
	{
		super(Console.GBA, Provider.OFFLINELIST, new Dimension(480,320), new Dimension(480,320), new URL("http://offlinelistgba.free.fr/imgs/"));
	}

	@Override
  public String downloadURL(Rom rom)
	{
		String query1 = "http://www.emuparadise.me/roms/search.php?query=";
		String query2 = "&section=roms&sysid=31";
		
		String name = rom.title.replaceAll("\\W", " ").toLowerCase();
		name = name.replace(" ","%20");
		// Renamer.formatNumber(rom.imageNumber)
	
		return query1+name+query2;
	}
}