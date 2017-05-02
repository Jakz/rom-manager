package jack.rm.workflow.organizers;

import java.nio.file.Path;
import java.util.function.Function;

import jack.rm.workflow.GameEntry;
import jack.rm.workflow.RomOperation;

public class OrganizeByLambda extends RomOperation
{
  private final Function<GameEntry, Path> lambda;
  
  public OrganizeByLambda(Function<GameEntry, Path> lambda)
  {
    this.lambda = lambda;
    
  }

  @Override
  public String getName() { return "Organizer by lambda"; }

  @Override
  public String getDescription() {
    return "Sets the destination path according to lambda";
  }

  @Override
  protected GameEntry doApply(GameEntry handle) throws Exception 
  { 
    handle.setFolder(() -> lambda.apply(handle));
    return handle;
  }
 
  
}
