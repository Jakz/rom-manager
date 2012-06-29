package jack.rm.data;

import java.util.*;

public enum Language
{
	
	FRENCH("French", 1),
	ENGLISH("English", 2),
	CHINESE("Chinese", 4),
	DANISH("Danish", 4),
	DUTCH("Dutch", 16),
	FINNISH("Finnish", 32),
	GERMAN("German", 64),
	ITALIAN("Italian", 128),
	JAPANESE("Japanese", 256),
	NORWEGIAN("Norwegian", 512),
	POLISH("Polish", 1024),
	PORTUGUESE("Portuguese", 2048),
	SPANISH("Spanish", 4096),
	SWEDISH("Swedish", 8192),
	ENGLISH_UK("English (UK)", 16384),
	PORTUGUESE_BR("Portuguese (BR)",32768),
	KOREAN("Korean", 65536)
	;
	
	public final String fullName;
	public final int code;
	
	private static final HashMap<String, Language> mapping = new HashMap<String, Language>();
	
	static
	{
		for (Language l : Language.values())
		{
			mapping.put(l.fullName,l);
		}
	}
	
	Language(String name, int code)
	{
		this.fullName = name;
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
