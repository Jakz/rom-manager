package jack.rm.workflow;

import jack.rm.data.rom.Attribute;

public class SortByAttributeOperation extends RomOperation
{
  private boolean isLowercase;
  private Attribute attribute;
  
  public SortByAttributeOperation(Attribute attrib, boolean isLowercase)
  {
    this.attribute = attrib;
    this.isLowercase = isLowercase;
  }
  
  public String getDescription() { return "Sorts ROM by a specific attribute while consolidating workflow"; }
  public String getName() { return "Sorter By Attribute"; }
  
  protected RomWorkflowEntry doApply(RomWorkflowEntry handle)
  {
    Object value = handle.getRom().getAttribute(attribute);
    String folder = value != null ? value.toString() : "Uncategorized";
    if (isLowercase)
      folder = folder.toLowerCase();
    
    handle.setDestPath(handle.getDestPath().resolve(folder));
    
    return handle;
  }
}
