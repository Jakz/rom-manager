package jack.rm.data.rom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import jack.rm.Settings;
import jack.rm.assets.Asset;
import jack.rm.assets.AssetData;
import jack.rm.data.attachment.Attachment;
import jack.rm.data.console.System;
import jack.rm.data.romset.RomSet;
import jack.rm.files.romhandles.RomHandle;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.RenamerPlugin;

public class Rom implements Comparable<Rom>
{
	private final RomSet set;
  
  public RomStatus status;
	
	private Map<Attribute, Object> attributes = new HashMap<>();
	private Map<Asset, AssetData> assetData = new HashMap<>();
	private Map<Attribute, Object> customAttributes = new HashMap<>();
	private List<Attachment> attachments = new ArrayList<>();
	private List<RomGroup> groups = new ArrayList<>();
	
	public void setAttribute(Attribute key, Object value) { attributes.put(key, value); }
	public void setCustomAttribute(Attribute key, Object value) { customAttributes.put(key, value); }
	
	@SuppressWarnings("unchecked") public <T> T getAttribute(Attribute key) { 
	  return (T)customAttributes.getOrDefault(key, attributes.get(key));
	}
	
	public boolean hasAttribute(Attribute key) {
	  return customAttributes.containsKey(key) || attributes.containsKey(key);
	}
	
  public Stream<Map.Entry<Attribute, Object>> getCustomAttributes() { return customAttributes.entrySet().stream(); }
  public boolean hasCustomAttribute(Attribute attrib) { return customAttributes.containsKey(attrib); }
  public void clearCustomAttribute(Attribute attrib) { customAttributes.remove(attrib); }
  
  public List<Attachment> getAttachments() { return attachments; }

	
  private boolean favourite;
		
	private RomHandle path;
	
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
	
	public void addToGroup(RomGroup group)
	{
	  group.addRom(this);
	  groups.add(group);
	}
	
	public boolean removeFromGroup(RomGroup group)
	{
	  boolean willBeEmpty = group.removeRom(this);
	  groups.remove(group);
	  return willBeEmpty;
	}
	
	public RomID<?> getID() { return new RomID.CRC(getCRC()); }
	
	public RomHandle getPath() { return path; }
	public void setPath(RomHandle path) { this.path = path; }
	
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
	  path = path.relocate(dest);
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
    RenamerPlugin renamer = set.getSettings().getRenamer();
    return renamer.getCorrectName(this);
  }
  
  public String getCorrectInternalName()
  {
    RenamerPlugin renamer = set.getSettings().getRenamer();
    return renamer.getCorrectInternalName(this);
  }
  
  public Path getCorrectFolder()
  {
    FolderPlugin mover = set.getSettings().getFolderOrganizer();
    return mover.getFolderForRom(this);
  }
  
  public boolean hasCorrectInternalName()
  {
    return !path.isArchive() || getCorrectInternalName().equals(path.plainInternalName());
  }
  
  public boolean hasCorrectName()
  {
    Settings settings = set.getSettings();
    
    boolean hasCorrectName = getCorrectName().equals(path.plainName());
    
    if (!settings.shouldRenameInternalName)
      return hasCorrectName;
    else
      return hasCorrectName && hasCorrectInternalName();
  }
  
  public boolean hasCorrectFolder()
  {
    try {
      return RomSet.current.getSettings().getFolderOrganizer() == null || 
        Files.isSameFile(path.file().getParent(), set.getSettings().romsPath.resolve(getCorrectFolder()));
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
