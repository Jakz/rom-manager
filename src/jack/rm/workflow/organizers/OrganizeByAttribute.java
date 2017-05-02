package jack.rm.workflow.organizers;

import java.nio.file.Paths;

import com.github.jakz.romlib.data.game.attributes.Attribute;

import jack.rm.workflow.GameEntry;
import jack.rm.workflow.RomOperation;

public class OrganizeByAttribute extends RomOperation
{
  private boolean isLowercase;
  private Attribute attribute;
  
  public OrganizeByAttribute(Attribute attrib, boolean isLowercase)
  {
    this.attribute = attrib;
    this.isLowercase = isLowercase;
  }
  
  public String getDescription() { return "Sorts ROM by a specific attribute while consolidating workflow"; }
  public String getName() { return "Sorter By Attribute"; }
  
  protected GameEntry doApply(GameEntry handle)
  {
    Object value = handle.getGame().getAttribute(attribute);
    String folder = value != null ? value.toString() : "Uncategorized";
    
    handle.setFolder(() -> Paths.get(isLowercase ? folder : folder.toLowerCase()));
    return handle;
  }
}
