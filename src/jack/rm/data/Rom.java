package jack.rm.data;

import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.RomSet;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Rom implements Comparable<Rom>
{
	private final RomSet set;
  
  public RomStatus status;
	
	private Map<RomAttribute, Object> attributes = new HashMap<>();
	private Map<Asset, AssetData> assetData = new HashMap<>();
	
	public void setAttribute(RomAttribute key, Object value) { attributes.put(key, value); }
	@SuppressWarnings("unchecked") public <T> T getAttribute(RomAttribute key) { return (T)attributes.get(key); }
	
  private boolean favourite;
		
	public Genre genre;

	private RomPath path;
	
	public Rom(RomSet set)
	{
    this.set = set;
	  status = RomStatus.MISSING;
	}
	
	public RomID<?> getID() { return new RomID.CRC(getCRC()); }
	
	public RomPath getPath() { return path; }
	public void setPath(RomPath path) { this.path = path; }
	
	public void setTitle(String title) { setAttribute(RomAttribute.TITLE, title); }
	public String getTitle() { return getAttribute(RomAttribute.TITLE); }
	
	public void setSize(RomSize size) { setAttribute(RomAttribute.SIZE, size); }
	public RomSize getSize() { return getAttribute(RomAttribute.SIZE); }
	
	public void setCRC(long crc) { setAttribute(RomAttribute.CRC, crc); }
	public long getCRC() { return getAttribute(RomAttribute.CRC); }
	
	@SuppressWarnings("unchecked")
	public Set<Language> getLanguages()
	{
	  return (Set<Language>)attributes.computeIfAbsent(RomAttribute.LANGUAGE, k -> new TreeSet<Language>());
	}
	
	public AssetData getAssetData(Asset asset)
	{
	  return assetData.computeIfAbsent(asset, k -> new AssetData());
	}
	
	@Override
  public String toString()
	{
		return getCorrectName();
	}
	
	public void move(Path dest) throws IOException
	{
	  Files.move(path.file(), dest);
	  path = path.build(dest);
	}
	
	public long getCRCforAsset(Asset asset)
	{
	  return assetData.get(asset).getCRC();
	}
	
	public boolean hasAsset(Asset asset)
	{
	  Path f = RomSet.current.getAssetPath(asset, this);
	   
    if (!Files.exists(f)) return false;
    else
    {
      if (asset.hasCRC())
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
	  for (Asset asset : RomSet.current.getAssetManager().getSupportedAssets())
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
	  if (set.doesSupportAttribute(RomAttribute.NUMBER) && other instanceof Rom)
	  {
	    int n1 = getAttribute(RomAttribute.NUMBER);
	    int n2 = ((Rom)other).getAttribute(RomAttribute.NUMBER);
	    return n1 == n2;
	  }
	  
	  return false;
	}
	
	@Override
  public int compareTo(Rom rom)
	{
		if (set.doesSupportAttribute(RomAttribute.NUMBER))
		{
      int n1 = getAttribute(RomAttribute.NUMBER);
      int n2 = rom.getAttribute(RomAttribute.NUMBER);
      
      return n1 - n2;
		}
	  
	  return getTitle().compareTo(rom.getTitle());
	}
		
	public boolean isFavourite() { return favourite; }
	public void setFavourite(boolean value) { favourite = value; }
}
