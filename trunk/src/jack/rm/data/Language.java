package jack.rm.data;

import java.util.*;

public enum Language
{
	
	FRENCH("French", "FR", 1),
	ENGLISH("English", "EN", 2),
	CHINESE("Chinese", "ZH", 4),
	DANISH("Danish", "DA", 4),
	DUTCH("Dutch", "NL", 16),
	FINNISH("Finnish", "FI", 32),
	GERMAN("German", "DE", 64),
	ITALIAN("Italian", "IT", 128),
	JAPANESE("Japanese", "JA", 256),
	NORWEGIAN("Norwegian", "NO", 512),
	POLISH("Polish", "PL", 1024),
	PORTUGUESE("Portuguese", "PT", 2048),
	SPANISH("Spanish", "ES", 4096),
	SWEDISH("Swedish", "SV", 8192),
	ENGLISH_UK("English (UK)", "EN", 16384),
	PORTUGUESE_BR("Portuguese (BR)", "PT", 32768),
	KOREAN("Korean", "KO", 65536)
	;
	
	public final String fullName;
	public final String iso639_1;
	public final int code;
	
	
	private static final HashMap<String, Language> mapping = new HashMap<String, Language>();
	
	static
	{
		for (Language l : Language.values())
		{
			mapping.put(l.fullName,l);
		}
	}
	
	Language(String name, String iso639_1, int code)
	{
		this.fullName = name;
		this.iso639_1 = iso639_1;
		this.code = code;
	}
	
	public String toString()
	{
		return fullName;
	}
	
	static Language forName(String language)
	{
		return mapping.get(language);
	}
}
