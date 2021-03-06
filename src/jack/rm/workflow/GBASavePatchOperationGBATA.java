package jack.rm.workflow;

import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;
import com.github.jakz.romlib.support.patches.GBASavePatcherGBATA;

public class GBASavePatchOperationGBATA extends DefaultGameOperation
{
  public String getName() { return "GBA Save Patcher"; }
  public String getDescription() { return "This operation patches all save types of a GBA rom to SRAM"; }
  
  public GBASavePatchOperationGBATA()
  {

  }
  
  protected GameEntry doApply(GameEntry handle) throws Exception
  {
    GBASavePatcherGBATA.patch(handle.getGame().getAttribute(GameAttribute.SAVE_TYPE), handle.getBuffer());
    return handle;
  }
  
  public boolean isPlatformSupported(Platform platform)
  {
    return platform == Platforms.GBA;
  }
  
}
