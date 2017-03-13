package jack.rm.workflow;

import jack.rm.files.Trimmer;

public class TrimOperation extends RomOperation
{
  byte[] filler;
  
  public String getName() { return "Rom Trimmer"; }
  public String getDescription() { return "This operation trims unused space from a ROM to reduce its final size"; }
  
  public TrimOperation(byte[] filler)
  {
    this.filler = filler;
  }
  
  protected RomWorkflowEntry doApply(RomWorkflowEntry rom)
  {
    try {
      Trimmer.trim(rom.getBuffer(), filler);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return rom;
  }
  
}