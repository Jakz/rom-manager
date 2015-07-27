package jack.rm.json.workflow;

import jack.rm.data.Rom;

import com.pixbits.workflow.WorkflowData;

public class WokrflowRom implements WorkflowData
{
  protected final Rom rom;

  public WokrflowRom(Rom rom)
  {
    this.rom = rom;
  }
  
  public Rom getRom() { return rom; }

}