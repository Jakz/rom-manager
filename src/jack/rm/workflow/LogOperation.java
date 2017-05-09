package jack.rm.workflow;

public class LogOperation extends DefaultGameOperation
{  
  public String getName() { return "Logger"; }
  public String getDescription() { return "This operation logs current target"; }
  
  public LogOperation()
  {
  }
  
  protected GameEntry doApply(GameEntry rom)
  {
    System.out.println("Working on "+rom.getGame().getTitle());
    return rom;
  }
  
}
