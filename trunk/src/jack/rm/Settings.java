package jack.rm;

import java.io.*;
import java.util.*;

import jack.rm.data.set.*;

public class Settings
{
	private static Map<RomSet, Settings> settings = new HashMap<RomSet, Settings>(); 
	
	public static Settings get(RomSet set)
	{
		Settings s = settings.get(set);
		
		if (s == null)
		{
			s = new Settings(set);
			settings.put(set, s);
		}
		
		return s;
	}
	
	public static Settings current()
	{
		return settings.get(RomSet.current);
	}
	
	public static void consolidate()
	{
		try
		{
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/settings.xml"));
		
			dos.writeUTF("<settings>\n");
			
			for (Settings s : settings.values())
			{
				dos.writeUTF(s.toXML());
			}
			
			dos.writeUTF("</settings>\n");
			
			dos.close();
		}
		catch (Exception e )
		{
			e.printStackTrace();
		}
	}
	
	public final RomSet set;
	
	public String renamingPattern;
	public String romsPath;
	public String unknownPath;
	
	public boolean checkImageCRC;
	public boolean checkInsideArchives;
	public boolean moveUnknownFiles;
	
	public boolean renameInsideZips;
	
	Settings(RomSet set)
	{
		this.set = set;
		
		checkImageCRC = true;
		checkInsideArchives = true;
		moveUnknownFiles = false;
		renameInsideZips = false;
		
		renamingPattern = "%n - %t [%S]";
		romsPath = null;
		unknownPath = null;
	}
	
	public String toXML()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\t<setting>\n");
		sb.append("\t\t<dat>").append(set.ident()).append("</dat>\n");
		sb.append("\t\t<romsPath>").append(romsPath).append("</romsPath>\n");
		sb.append("\t\t<unknownPath>").append(unknownPath).append("</unknownPath>\n");
		sb.append("\t\t<pattern>").append(renamingPattern).append("</pattern>\n");
		
		sb.append("\t\t<checkImageCRC>").append(checkImageCRC).append("</checkImageCRC>\n");
		sb.append("\t\t<checkInsideArchives>").append(checkInsideArchives).append("</checkInsideArchives>\n");
		sb.append("\t\t<moveUnknownFiles>").append(moveUnknownFiles).append("</moveUnknownFiles>\n");
		sb.append("\t\t<renameInsideZips>").append(renameInsideZips).append("</renameInsideZips>\n");
		sb.append("\t</setting>\n");
		
		return sb.toString();
	}
}
