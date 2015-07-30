package jack.rm.plugins.providers;

import jack.rm.data.romset.ProviderType;

public class OfflineListProviderType implements ProviderType
{
  public String getIdent() { return "ol"; }
  public String getExtension() { return "xml"; }
}
