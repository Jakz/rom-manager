package jack.rm.json.workflow;

import com.pixbits.workflow.Mutuator;
import jack.rm.data.console.System;

public abstract class RomOperation implements Mutuator<RomHandle>
{  
  abstract String getName();
  abstract String getDescription();
  boolean isSystemSupported(System system) { return true; }
}
