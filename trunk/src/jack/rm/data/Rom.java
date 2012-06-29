package jack.rm.data;

import jack.rm.Paths;
import java.io.*;

public class Rom implements Comparable<Rom>
{
	public RomStatus status;
	
	public int number;
	public int imageNumber;
	
	public String title;
	public String publisher;
	public String group;
	public String date;
	public RomSize size;
	
	public Location location;
	public int languages;
	public Genre genre;
	
	public String internalName;
	public String serial;
	public long crc;
	
	public String save;
	public RomSave saveType;
	
	public String info;
	
	public File path;
	public RomType type;
	
	public long imgCRC1;
	public long imgCRC2;
	
	public Rom()
	{
		status = RomStatus.NOT_FOUND;
	}
	
	public Rom(int number)
	{
		this();
		this.number = number;
	}
	
	public String toString()
	{
		return Renamer.getCorrectName(this);
	}
	
	public String languagesAsString()
	{
		String s = "";
		boolean first = true;
		
		for (Language l : Language.values())
			if ((languages & l.code) != 0)
			{
				if (!first)
					s += ", ";
				else
					first = false;
				
				s += l.fullName;
			}
				
		return s;
	}
	
	public String saveType()
	{
		if (saveType != null)
			return " ("+saveType.name+")";
		else
			return "";
	}
	
	public boolean hasTitleArt()
	{
		return new File(Paths.screensTitle()+Renamer.formatNumber(imageNumber)+".png").exists();
	}
	
	public boolean hasGameArt()
	{
		return new File(Paths.screensGame()+Renamer.formatNumber(imageNumber)+".png").exists();
	}
	
	public int compareTo(Rom rom)
	{
		return number < rom.number ? -1 : (number == rom.number ? 0 : 1);
	}
}
