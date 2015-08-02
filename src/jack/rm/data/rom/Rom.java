package jack.rm.data.rom;

import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.romset.RomSet;
import jack.rm.files.Scanner;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;
import jack.rm.data.console.System;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Rom implements Comparable<Rom>
{
	private final RomSet set;
  
  public RomStatus status;
	
	private Map<Attribute, Object> attributes = new HashMap<>();
	private Map<Asset, AssetData> assetData = new HashMap<>();
	private Map<Attribute, Object> customAttributes = new HashMap<>();
	
	public void setAttribute(Attribute key, Object value) { attributes.put(key, value); }
	public void setCustomAttribute(Attribute key, Object value) { customAttributes.put(key, value); }
	
	@SuppressWarnings("unchecked") public <T> T getAttribute(Attribute key) { 
	  return (T)customAttributes.getOrDefault(key, attributes.get(key));
	}
	
  public Stream<Map.Entry<Attribute, Object>> getCustomAttributes() { return customAttributes.entrySet().stream(); }
  public boolean hasCustomAttribute(Attribute attrib) { return customAttributes.containsKey(attrib); }
  public void clearCustomAttribute(Attribute attrib) { customAttributes.remove(attrib); }

	
  private boolean favourite;
		
	private RomPath path;
	
	public Rom(RomSet set)
	{
    this.set = set;
	  status = RomStatus.MISSING;
	}
	
	public RomSet getRomSet() { return set; }
	
	public boolean shouldSerializeState()
	{
	  return isFavourite() || status != RomStatus.MISSING || !customAttributes.isEmpty();
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
	
	public System getSystem() { return set.system; }
	
	@SuppressWarnings("unchecked")
	public Set<Language> getLanguages()
	{
	  return (Set<Language>)attributes.computeIfAbsent(RomAttribute.LANGUAGE, k -> new TreeSet<Language>());
	}
	
	public AssetData getAssetData(Asset asset)
	{
	  return assetData.computeIfAbsent(asset, k -> new AssetData(k, this));
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
	
	public boolean hasAsset(Asset asset)
	{
	  return getAssetData(asset).isPresent();
	}
	
	public boolean hasAllAssets()
	{
	  for (Asset asset : RomSet.current.getAssetManager().getSupportedAssets())
	    if (!hasAsset(asset))
	      return false;
	  
	  return true;
	}
	
	public void updateStatus()
	{
	  if (status != RomStatus.MISSING)
	  {
	    status = isOrganized() ? RomStatus.FOUND : RomStatus.UNORGANIZED;
	  }
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
    try {
      return RomSet.current.getSettings().getFolderOrganizer() == null || 
        Files.isSameFile(path.file().getParent(), RomSet.current.getSettings().romsPath.resolve(getCorrectFolder()));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return false;
    }
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
	  else if (other instanceof Rom)
	  {
	    return getTitle().equals(((Rom)other).getAttribute(RomAttribute.TITLE));
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
