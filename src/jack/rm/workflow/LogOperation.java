package jack.rm.workflow;

public class LogOperation extends RomOperation
{  
  public String getName() { return "Logger"; }
  public String getDescription() { return "This operation logs current target"; }
  
  public LogOperation()
  {
  }
  
  protected RomWorkflowEntry doApply(RomWorkflowEntry rom)
  {
    System.out.println("Working on "+rom.getRom().getTitle());
    return rom;
  }
  
}
