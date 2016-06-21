package jack.rm.plugins.providers;

import jack.rm.data.romset.DatFormat;

public class ClrMameDatFormat implements DatFormat
{
  public String getIdent() { return "cm"; }
  public String getExtension() { return "dat"; }
}
