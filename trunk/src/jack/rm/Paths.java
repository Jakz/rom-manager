package jack.rm;

import jack.rm.data.set.RomSet;

import java.io.*;

public class Paths
{
	//public static String roms = "roms/";//"/Volumes/Vicky/Roms/roms/gba/";
	public static String unknown = "unknown/";

	//public static String screensTitleURL_AS = "http://advanscene.com/html/Releases/snap_a/";
	//public static String screensGameURL_AS = "http://advanscene.com/html/Releases/snap_b/";

	public static String screensTitle()
	{
		return "screens/"+RomSet.current.name+"/title/";
	}
	
	public static String screensGame()
	{
		return "screens/"+RomSet.current.name+"/game/";
	}
	
	static
	{
		new File(screensTitle()).mkdirs();
		new File(screensGame()).mkdirs();
		new File(RomSet.current.romPath).mkdirs();
	}
}
