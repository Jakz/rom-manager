package jack.rm.workflow;

import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.Platform;

import jack.rm.files.GBASavePatcherGBATA;

public class GBASavePatchOperationGBATA extends RomOperation
{
  public String getName() { return "GBA Save Patcher"; }
  public String getDescription() { return "This operation patches all save types of a GBA rom to SRAM"; }
  
  public GBASavePatchOperationGBATA()
  {

  }
  
  protected RomWorkflowEntry doApply(RomWorkflowEntry handle) throws Exception
  {
    GBASavePatcherGBATA.patch(handle.getGame().getAttribute(GameAttribute.SAVE_TYPE), handle.getBuffer());
    return handle;
  }
  
  public boolean isPlatformSupported(Platform platform)
  {
    return platform == Platform.GBA;
  }
  
}
