package jack.rm.data.rom;

import jack.rm.i18n.*;
import jack.rm.gui.Icon;
import java.util.*;

public enum Location
{
	EUROPE(0, Text.LOCATION_EUROPE, "EU", "E", Icon.FLAG_EUROPE),
	USA(1, Text.LOCATION_USA, "US", "U", Icon.FLAG_USA),
	GERMANY(2, Text.LOCATION_GERMANY, "GE", "G", Icon.FLAG_GERMANY),
	CHINA(3, Text.LOCATION_CHINA, "CH", "C", Icon.FLAG_CHINA),
	SPAIN(4, Text.LOCATION_SPAIN, "SP", "S", Icon.FLAG_SPAIN),
	FRANCE(5, Text.LOCATION_FRANCE, "FR", "F", Icon.FLAG_FRANCE),
	ITALY(6, Text.LOCATION_ITALY, "IT", "I", Icon.FLAG_ITALY),
	JAPAN(7, Text.LOCATION_JAPAN, "JP", "J", Icon.FLAG_JAPAN),
	NETHERLANDS(8, Text.LOCATION_NETHERLANDS, "NL", "N", Icon.FLAG_NETHERLANDS), 
	AUSTRALIA(19, Text.LOCATION_AUSTRALIA, "AU", "A", Icon.FLAG_AUSTRALIA),
	KOREA(22, Text.LOCATION_KOREA, "KR", "K", Icon.FLAG_KOREA),
	
	NONE(-1, Text.NONE, "NA", "N", Icon.FLAG_USA)
	
	;
	
	private static final HashMap<Integer,Location> mapping = new HashMap<Integer,Location>();
	
	static
	{
		mapping.put(0,EUROPE);
		mapping.put(1,USA);
		mapping.put(2,GERMANY);
		mapping.put(3,CHINA);
		mapping.put(4,SPAIN);
		mapping.put(5,FRANCE);
		mapping.put(6,ITALY);
		mapping.put(7,JAPAN);
		mapping.put(8,NETHERLANDS);
		mapping.put(19,AUSTRALIA);
		mapping.put(22,KOREA);
		
		mapping.put(16,JAPAN);
		mapping.put(18,JAPAN);
	}
	
	public final int index;
	public final String fullName;
	public final String shortName;
	public final String tinyName;
	public final Icon icon;
	
	Location(int index, Text name, String shortName, String tinyName, Icon icon)
	{
		this.index = index;
		this.fullName = name.text();
		this.shortName = shortName;
		this.tinyName = tinyName;
		this.icon = icon;
	}
	
	public static Location get(int index)
	{
		Location l = mapping.get(index);
		
		if (l != null)
			return l;
		
		//System.out.println("[ERROR] Location "+index+" undefined.");
		
		return Location.NONE;
	}
	
	@Override
  public String toString()
	{
		return fullName;
	}
}
