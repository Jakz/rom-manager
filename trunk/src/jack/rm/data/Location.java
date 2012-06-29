package jack.rm.data;

import javax.swing.ImageIcon;
import jack.rm.i18n.*;
import java.util.*;

public enum Location
{
	EUROPE(0, Text.LOCATION_EUROPE, "EU", "E", "flag_europe"),
	USA(1, Text.LOCATION_USA, "US", "U", "flag_usa"),
	GERMANY(2, Text.LOCATION_GERMANY, "GE", "G", "flag_germany"),
	CHINA(3, Text.LOCATION_CHINA, "CH", "C", "flag_china"),
	SPAIN(4, Text.LOCATION_SPAIN, "SP", "S", "flag_spain"),
	FRANCE(5, Text.LOCATION_FRANCE, "FR", "F", "flag_france"),
	ITALY(6, Text.LOCATION_ITALY, "IT", "I", "flag_italy"),
	JAPAN(7, Text.LOCATION_JAPAN, "JP", "J", "flag_japan"),
	NETHERLANDS(8, Text.LOCATION_NETHERLANDS, "NL", "N", "flag_netherlands"), 
	AUSTRALIA(19, Text.LOCATION_AUSTRALIA, "AU", "A", "flag_australia"),
	KOREA(22, Text.LOCATION_KOREA, "KR", "K", "flag_korea"),
	
	NONE(-1, Text.NONE, "NA", "N", "flag_usa")
	
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
	public final ImageIcon icon;
	
	Location(int index, Text name, String shortName, String tinyName, String imgName)
	{
		this.index = index;
		this.fullName = name.text();
		this.shortName = shortName;
		this.tinyName = tinyName;
		this.icon = new ImageIcon("images/"+imgName+".png");
	}
	
	public static Location get(int index)
	{
		Location l = mapping.get(index);
		
		if (l != null)
			return l;
		
		//System.out.println("[ERROR] Location "+index+" undefined.");
		
		return Location.NONE;
	}
	
	public String toString()
	{
		return fullName;
	}
}
