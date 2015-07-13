package jack.rm.data;

import jack.rm.Settings;
import jack.rm.files.FolderPolicy;
import jack.rm.files.Organizer;
import jack.rm.files.RenamePolicy;

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
	
	public boolean isOrganized()
	{
	  boolean nameIsOrganized = !Settings.current().organizer.hasRenamePolicy() || hasCorrectName();
	  boolean positionIsOrganized = !Settings.current().organizer.hasFolderPolicy() || hasCorrectFolder();
	  return nameIsOrganized && positionIsOrganized;
	}
	
	public boolean hasCorrectFolder()
	{
	  return Organizer.getCorrectFolder(this).equals(entry.file().getParent());
	}
	
	public boolean hasCorrectName()
	{
	  return Organizer.getCorrectName(this).equals(entry.plainName());
	}
	
	@Override
	public boolean equals(Object other)
	{
	  return other instanceof Rom && ((Rom)other).number == number;
	}
	
	@Override
  public int compareTo(Rom rom)
	{
		return number - rom.number;
	}
}
