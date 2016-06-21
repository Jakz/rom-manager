package jack.rm.plugins.providers;

import jack.rm.data.romset.DatFormat;

public class OfflineListDatFormat implements DatFormat
{
  public String getIdent() { return "ol"; }
  public String getExtension() { return "xml"; }
}
