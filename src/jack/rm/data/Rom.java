package jack.rm.data;

import jack.rm.Settings;
import jack.rm.data.set.RomSet;
import jack.rm.files.Organizer;

import java.io.IOException;
import java.nio.file.*;

public class Rom implements Comparable<Rom>
{
	public RomStatus status;
	
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
	
	public RomSave<?> save;
	
	public String info;
	
	public RomPath entry;
	
	public long imgCRC1;
	public long imgCRC2;
	
	public Rom()
	{
		status = RomStatus.NOT_FOUND;
    imgCRC1 = -1L;
    imgCRC2 = -1L;
	}
	
	public RomID<?> getID() { return new RomID.CRC(crc); }
	
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
	
	public void move(Path dest) throws IOException
	{
	  Files.move(entry.file(), dest);
	  entry = entry.build(dest);
	}
	
	public long getCRCforAsset(Asset asset)
	{
	  return asset == Asset.SCREEN_GAMEPLAY ? imgCRC2 : imgCRC1;
	}
	
	public boolean hasAsset(Asset asset)
	{
	  Path f = Settings.getAssetPath(asset).resolve(Organizer.formatNumber(imageNumber)+".png");
	   
    if (!Files.exists(f)) return false;
    else
    {
      if (Settings.current().checkImageCRC)
      {
        long icrc = Scanner.computeCRC(f);
        return icrc == getCRCforAsset(asset);
      }
      else
        return true;
    }
	}
	
	public boolean hasAllAssets()
	{
	  for (Asset asset : RomSet.current.getSupportedAssets())
	    if (!hasAsset(asset))
	      return false;
	  
	  return true;
	}

	public boolean isOrganized()
	{
	  boolean nameIsOrganized = !Settings.current().organizer.hasRenamePolicy() || hasCorrectName();
	  boolean positionIsOrganized = Settings.current().getFolderOrganizer() == null || hasCorrectFolder();
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
	  return other instanceof Rom && ((Rom)other).title.equals(title);
	}
	
	@Override
  public int compareTo(Rom rom)
	{
		return title.compareTo(rom.title);
	}
}
