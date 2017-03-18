package jack.rm.log;

import com.pixbits.lib.log.LogScope;

public enum LogSource implements LogScope
{
  STATUS,
  DOWNLOADER,
  ORGANIZER,
  SCANNER,
  IMPORTER,
  PLUGINS,
  DAT_DOWNLOADER
  ;
  
  public String toString() { return this.name().toLowerCase(); }
}
