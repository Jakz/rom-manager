package jack.rm.plugins.providers.offlinelist;

import jack.rm.data.console.System;
import jack.rm.data.set.RomSet;
import jack.rm.data.set.Provider;

public class OfflineListProvider implements Provider
{
  public String getName() { return "OfflineList"; }
  public String getTag() { return "ol"; }
}
