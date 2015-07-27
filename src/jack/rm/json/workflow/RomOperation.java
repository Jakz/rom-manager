package jack.rm.json.workflow;

import com.pixbits.workflow.Mutuator;

public interface RomOperation extends Mutuator<RomHandle>
{
  String getName();
  String getDescription();
  default boolean isSystemSupported() { return true; }
}
