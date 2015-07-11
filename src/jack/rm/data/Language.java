package jack.rm.data;

import java.util.*;
import jack.rm.gui.Icon;

public enum Language
{
	
	FRENCH("French", "FR", 1, Icon.FLAG_FRANCE),
	ENGLISH("English", "EN", 2, Icon.FLAG_USA),
	CHINESE("Chinese", "ZH", 4, Icon.FLAG_CHINA),
	DANISH("Danish", "DA", 4, Icon.FLAG_DENMARK),
	DUTCH("Dutch", "NL", 16, Icon.FLAG_NETHERLANDS),
	FINNISH("Finnish", "FI", 32, Icon.FLAG_FINLAND),
	GERMAN("German", "DE", 64, Icon.FLAG_GERMANY),
	ITALIAN("Italian", "IT", 128, Icon.FLAG_ITALY),
	JAPANESE("Japanese", "JA", 256, Icon.FLAG_JAPAN),
	NORWEGIAN("Norwegian", "NO", 512, Icon.FLAG_NORWAY),
	POLISH("Polish", "PL", 1024, Icon.FLAG_POLAND),
	PORTUGUESE("Portuguese", "PT", 2048, Icon.FLAG_PORTUGAL),
	SPANISH("Spanish", "ES", 4096, Icon.FLAG_SPAIN),
	SWEDISH("Swedish", "SV", 8192, Icon.FLAG_SWEDEN),
	ENGLISH_UK("English (UK)", "EN", 16384, Icon.FLAG_UNITED_KINGDOM),
	PORTUGUESE_BR("Portuguese (BR)", "PT", 32768, Icon.FLAG_BRAZIL),
	KOREAN("Korean", "KO", 65536, Icon.FLAG_KOREA)
	;
	
	public final String fullName;
	public final String iso639_1;
	public final int code;
	public final Icon icon;
	
	
	private static final HashMap<String, Language> mapping = new HashMap<String, Language>();
	
	static
	{
		for (Language l : Language.values())
		{
			mapping.put(l.fullName,l);
		}
	}
	
	Language(String name, String iso639_1, int code, Icon icon)
	{
    this.fullName = name;
    this.iso639_1 = iso639_1;
    this.code = code;
	  this.icon = icon;
	}
	
	Language(String name, String iso639_1, int code)
	{
		this(name, iso639_1, code, null);
	}
	
	@Override
  public String toString()
	{
		return fullName;
	}
	
	static Language forName(String language)
	{
		return mapping.get(language);
	}
}
