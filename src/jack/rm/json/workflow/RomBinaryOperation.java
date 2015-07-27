package jack.rm.json.workflow;

import com.pixbits.workflow.Mutuator;

public interface RomBinaryOperation extends Mutuator<WorkflowBinaryRom>
{
  String getName();
  String getDescription();
  default boolean isSystemSupported() { return true; }
}
