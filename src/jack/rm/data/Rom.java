package jack.rm.data;

import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.RomWithSaveMixin;
import jack.rm.data.set.RomSet;
import jack.rm.files.Organizer;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Rom implements Comparable<Rom>, RomWithSaveMixin<RomSave<?>>
{
	public RomStatus status;
	
	private Map<RomAttribute, Object> attributes = new HashMap<>();
	
	public void setAttribute(RomAttribute key, Object value) { attributes.put(key, value); }
	@SuppressWarnings("unchecked") public <T> T getAttribute(RomAttribute key) { return (T)attributes.get(key); }
	
  private boolean favourite;

	public int imageNumber;
	
	public RomSize size;
	
	public Set<Language> languages;
	public Genre genre;
	
	public String serial;
	public long crc;
			
	private RomPath path;
	
	public long imgCRC1;
	public long imgCRC2;
	
	public Rom()
	{
		status = RomStatus.MISSING;
		languages = new TreeSet<>();
    imgCRC1 = -1L;
    imgCRC2 = -1L;
	}
	
	public RomID<?> getID() { return new RomID.CRC(crc); }
	
	public RomPath getPath() { return path; }
	public void setPath(RomPath path) { this.path = path; }
	
	public void setTitle(String title) { setAttribute(RomAttribute.TITLE, title); }
	public String getTitle() { return getAttribute(RomAttribute.TITLE); }
	
	@Override
  public String toString()
	{
		return getCorrectName();
	}
	
	public String languagesAsString()
	{
		return languages.stream().map( l -> l.fullName).collect(Collectors.joining(", "));
	}
	
	public void move(Path dest) throws IOException
	{
	  Files.move(path.file(), dest);
	  path = path.build(dest);
	}
	
	public long getCRCforAsset(Asset asset)
	{
	  return asset == Asset.SCREEN_GAMEPLAY ? imgCRC2 : imgCRC1;
	}
	
	public boolean hasAsset(Asset asset)
	{
	  Path f = RomSet.current.getAssetPath(asset).resolve(Organizer.formatNumber(imageNumber)+".png");
	   
    if (!Files.exists(f)) return false;
    else
    {
      if (RomSet.current.getSettings().checkImageCRC)
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
	  boolean name = hasCorrectName(), folder = hasCorrectFolder();
	  return name && folder;
	}
  
  public String getCorrectName()
  {
    RenamerPlugin renamer = RomSet.current.getSettings().getRenamer();
    return renamer.getCorrectName(this);
  }
  
  public Path getCorrectFolder()
  {
    FolderPlugin mover = RomSet.current.getSettings().getFolderOrganizer();
    return mover.getFolderForRom(this);
  }
  
  public boolean hasCorrectName()
  {
    return getCorrectName().equals(path.plainName());
  }
  
  public boolean hasCorrectFolder()
  {
    return RomSet.current.getSettings().getFolderOrganizer() == null || 
        path.file().getParent().equals(RomSet.current.getSettings().romsPath.resolve(getCorrectFolder()));
  }

	@Override
	public boolean equals(Object other)
	{
	  return other instanceof Rom && ((Rom)other).getTitle().equals(getTitle());
	}
	
	@Override
  public int compareTo(Rom rom)
	{
		return getTitle().compareTo(rom.getTitle());
	}
		
	public boolean isFavourite() { return favourite; }
	public void setFavourite(boolean value) { favourite = value; }
}
