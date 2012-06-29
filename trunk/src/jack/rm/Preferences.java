package jack.rm;

import java.util.*;

public class Preferences
{
	private Map<String, Object> preferences = new HashMap<String, Object>();
	
	Preferences()
	{
		preferences.put("check-art-crc", true);
	}
	
	public String renamerPattern;
	
	public boolean organizeRomsByNumber = true;
	public boolean organizeRomsDeleteEmptyFolders = true;
	public int organizeRomsByNumberFolderSize = 100;

	public String screensPath = "images/";
	
	public boolean booleanSetting(String name)
	{
		return (Boolean)preferences.get(name);
	}
}
