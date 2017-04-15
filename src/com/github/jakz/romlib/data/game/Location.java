package com.github.jakz.romlib.data.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.jakz.romlib.ui.Icon;
import com.github.jakz.romlib.ui.i18n.I18N;

import jack.rm.i18n.Text;


public enum Location
{
  USA(Text.LOCATION_USA, "US", "U", Icon.FLAG_USA, Language.ENGLISH),
	GERMANY(Text.LOCATION_GERMANY, "GE", "G", Icon.FLAG_GERMANY, Language.GERMAN),
	CHINA(Text.LOCATION_CHINA, "CH", "C", Icon.FLAG_CHINA, Language.CHINESE),
	SPAIN(Text.LOCATION_SPAIN, "SP", "S", Icon.FLAG_SPAIN, Language.SPANISH),
	CANADA(Text.LOCATION_CANADA, "CA", "CA", Icon.FLAG_CANADA, Language.ENGLISH),
	FRANCE(Text.LOCATION_FRANCE, "FR", "F", Icon.FLAG_FRANCE, Language.FRENCH),
	ITALY(Text.LOCATION_ITALY, "IT", "I", Icon.FLAG_ITALY, Language.ITALIAN),
	JAPAN(Text.LOCATION_JAPAN, "JP", "J", Icon.FLAG_JAPAN, Language.JAPANESE),
	NETHERLANDS(Text.LOCATION_NETHERLANDS, "NL", "N", Icon.FLAG_NETHERLANDS, Language.DUTCH), 
	DENMARK(Text.LOCATION_DENMARK, "DK", "DK", Icon.FLAG_DENMARK, Language.DANISH), 
	SWEDEN(Text.LOCATION_SWEDEN, "KR", "K", Icon.FLAG_SWEDEN, Language.SWEDISH),
	NORWAY(Text.LOCATION_NORWAY, "NO", "NO", Icon.FLAG_NORWAY, Language.NORWEGIAN),
	
	RUSSIA(Text.LOCATION_RUSSIA, "RU", "R", Icon.FLAG_JAPAN, Language.RUSSIAN), // TODO: flag

	AUSTRALIA(Text.LOCATION_AUSTRALIA, "AU", "A", Icon.FLAG_AUSTRALIA, Language.ENGLISH), // TODO: english_au?
	
	KOREA(Text.LOCATION_KOREA, "SW", "SW", Icon.FLAG_KOREA, Language.KOREAN),
	TAIWAN(Text.LOCATION_TAIWAN, "TW", "TW", Icon.FLAG_CHINA, Language.CHINESE), // TODO: taiwan flag
	HONG_KONG(Text.LOCATION_TAIWAN, "HK", "HK", Icon.FLAG_CHINA, Language.CHINESE), // TODO: hong kong flag

	
	BRASIL(Text.LOCATION_BRAZIL, "BR", "BR", Icon.FLAG_BRAZIL, Language.PORTUGUESE_BR),

	EUROPE(Text.LOCATION_EUROPE, "EU", "E", Icon.FLAG_EUROPE, ITALY, FRANCE, GERMANY, SPAIN, SWEDEN),
	USA_JAPAN(Text.LOCATION_USA_JAPAN, "US-JP", "UJ", Icon.FLAG_JAPAN_USA, USA, JAPAN),
	JAPAN_EUROPE(Text.LOCATION_JAPAN_EUROPE, "JP-EU", "JE", Icon.FLAG_JAPAN_USA, JAPAN, EUROPE), //TODO japan europe icon
	USA_EUROPE(Text.LOCATION_USA_EUROPE, "US-EU", "UE", Icon.FLAG_USA_EUROPE, USA, EUROPE),
	ASIA(Text.LOCATION_ASIA, "Asia", "Asia", Icon.FLAG_JAPAN, JAPAN, CHINA, HONG_KONG, TAIWAN), // TODO verify
	
	WORLD(Text.LOCATION_WORLD, "W", "W", Icon.FLAG_WORLD, USA, JAPAN, EUROPE),
	
	NONE(Text.NONE, "NA", "N", Icon.FLAG_USA)
	
	;
		
  public final long mask;
	public final String fullName;
	public final String shortName;
	public final String tinyName;
	public final Icon icon;
	public final Language language;
	
	Location(I18N name, String shortName, String tinyName, Icon icon, Language language)
	{
		if (this.ordinal() >= Long.BYTES*8)
		  new EnumConstantNotPresentException(Location.class, "Maximum amount of locations is " + (Long.BYTES*8));
		
    this.fullName = name.text();
    this.shortName = shortName;
    this.tinyName = tinyName;
    this.icon = icon;
		this.mask = 1L << this.ordinal();
		this.language = language;
	}
	
  private Location(I18N name, String shortName, String tinyName, Icon icon, Location... locations)
  { 
    this.fullName = name.text();
    this.shortName = shortName;
    this.tinyName = tinyName;
    this.icon = icon;
    this.mask = Arrays.stream(locations).reduce(0L, (m,l) -> m | l.mask, (u,v) -> u | v); 
    this.language = null;
  }
  
  public boolean isComposite() { return Long.bitCount(mask) > 1; }
  
  private static final Map<Long, Location> mapping = new HashMap<Long, Location>();
  
  static
  {
    for (Location location : values())
      mapping.put(location.mask, location);
  }
  
  public static Location getExactLocation(LocationSet set)
  {
    return mapping.getOrDefault(set.getMask(), Location.NONE);
  }
  
  public static List<Location> getFuzzyLocations(LocationSet set)
  {
    int matching = 0;
    final List<Location> locations = new ArrayList<>();
    
    for (Location location : locations)
    {
      long masked = set.getMask() & location.mask;
      if (masked > matching)
        locations.clear();
      if (masked >= matching)
        locations.add(location);
    }
    
    return locations;
  }
  
  public static Location forName(String string)
  {
    return Arrays.stream(values())
      .filter(l -> l.fullName.compareToIgnoreCase(string) == 0)
      .findFirst()
      .orElse(null);
  }
	
	@Override
  public String toString()
	{
		return fullName;
	}
}
