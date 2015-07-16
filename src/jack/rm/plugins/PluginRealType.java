package jack.rm.plugins;

import com.pixbits.plugin.PluginType;

public enum PluginRealType implements PluginType
{
  FOLDER_ORGANIZER { 
    public String toString() { return "Folder Organizer"; }
    public boolean isMutuallyExclusive() { return true; }
  },
  ROMSET_CLEANUP { 
    public String toString() { return "Romset Cleanup"; }
  },
  PATTERN_SET {
    public String toString() { return "Renamer Pattern Set"; }
  },
  RENAMER {
    public String toString() { return "Renamer"; }
    public boolean isMutuallyExclusive() { return true; }
  }
  
  ;
  
  public boolean isMutuallyExclusive() { return false; }
}
