package jack.rm.plugins.providers;

import jack.rm.data.set.ProviderType;

public class OfflineListProviderType implements ProviderType
{
  public String getIdent() { return "ol"; }
  public String getExtension() { return "xml"; }
}
