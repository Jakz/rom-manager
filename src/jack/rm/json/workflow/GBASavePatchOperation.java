package jack.rm.json.workflow;

import jack.rm.data.rom.RomAttribute;
import jack.rm.files.GBASavePatcher;
import jack.rm.files.Trimmer;

public class GBASavePatchOperation implements RomOperation
{
  public String getName() { return "GBA Save Patcher"; }
  public String getDescription() { return "This operation patches all save types of a GBA rom to SRAM"; }
  
  public GBASavePatchOperation()
  {

  }
  
  public RomHandle apply(RomHandle handle)
  {
    GBASavePatcher.patch(handle.getRom().getAttribute(RomAttribute.SAVE_TYPE), handle.getBuffer());
    return handle;
  }
  
}
