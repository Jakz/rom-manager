package jack.rm.plugins;

import com.pixbits.lib.plugin.PluginType;

public enum PluginRealType implements PluginType<PluginRealType>
{
  FOLDER_ORGANIZER("Folder Organizer", true, false),
  ROMSET_CLEANUP("Romset Cleanup", false, false),
  PATTERN_SET("Renamer Pattern Set", false, false),
  RENAMER("Renamer", true, true),
  ROM_DOWNLOADER("ROM Downloader", false, false),
  DAT_PARSER("DAT Parser", false, false),
  PROVIDER("DAT Provider", false, false),
  SEARCH("Search", true, false),
  SEARCH_PREDICATES("Search Predicates", false, false),
  SCANNER("Scanner", true, true),
  VERIFIER("Verifier", false, true),
  CATALOGUER("Cataloguer", false, false),
  FORMAT_SUPPORT("Format Support", false, false), // TODO: better name?
  DATA_FETCHER("Data Fetcher", false, false), // TODO: better name?
  MISC("Misc", false, false),
  ;
  
  public final String caption;
  public final boolean isMutuallyExclusive;
  public final boolean isRequired;
  
  PluginRealType(String name, boolean isMutuallyExclusive, boolean isRequired)
  {
    this.caption = name;
    this.isMutuallyExclusive = isMutuallyExclusive;
    this.isRequired = isRequired;
  }
  
  public String toString() { return caption; }
  public boolean isMutuallyExclusive() { return isMutuallyExclusive; }
  public boolean isRequired() { return isRequired; }
}
