package jack.rm.json.workflow;

public class LogOperation extends RomOperation
{  
  public String getName() { return "Logger"; }
  public String getDescription() { return "This operation logs current target"; }
  
  public LogOperation()
  {
  }
  
  public RomHandle apply(RomHandle rom)
  {
    System.out.println("Working on "+rom.getRom().getTitle());
    return rom;
  }
  
}
