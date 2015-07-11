package jack.rm.data;

import jack.rm.Settings;
import jack.rm.files.Organizer;

import java.nio.file.*;

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
	
	public RomFileEntry entry;
	
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
		
		imgCRC1 = -1L;
		imgCRC2 = -1L;
		
	}
	
	@Override
  public String toString()
	{
		return Organizer.getCorrectName(this);
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
		Path f = Settings.screensTitle().resolve(Organizer.formatNumber(imageNumber)+".png");

		if (!Files.exists(f)) return false;
		else
		{
			if (Settings.current().checkImageCRC)
			{
				long icrc = Scanner.computeCRC(f);
				return icrc == imgCRC1;
			}
			else
				return true;
		}
	}
	
	public boolean hasGameArt()
	{
    Path f = Settings.screensGame().resolve(Organizer.formatNumber(imageNumber)+".png");
    
    if (!Files.exists(f)) return false;
		else
		{
			if (Settings.current().checkImageCRC)
			{
				long icrc = Scanner.computeCRC(f);
				return icrc == imgCRC2;
			}
			else
				return true;
		}
	}
	
	@Override
  public int compareTo(Rom rom)
	{
		return number < rom.number ? -1 : (number == rom.number ? 0 : 1);
	}
}
