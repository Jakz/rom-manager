package jack.rm.plugins.providers;

import jack.rm.data.set.ProviderType;

public class ClrMameProviderType implements ProviderType
{
  public String getIdent() { return "cm"; }
  public String getExtension() { return "dat"; }
}
