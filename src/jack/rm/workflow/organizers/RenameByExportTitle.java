package jack.rm.workflow.organizers;

import jack.rm.workflow.GameEntry;
import jack.rm.workflow.DefaultGameOperation;

public class RenameByExportTitle extends DefaultGameOperation
{

  @Override
  public String getName() { 
    return "Renamer by export title";
  }

  @Override
  public String getDescription() { 
    return "Renames the final game according to export title attribute";
  }

  @Override
  protected GameEntry doApply(GameEntry handle) throws Exception {
    handle.setFileName(() -> 
      handle.getGame().getExportTitle() + "." + 
      handle.getGame().getPlatform().exts[0]);
    return handle;
  }

}
