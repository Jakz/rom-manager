package jack.rm.plugins;

import jack.rm.plugin.PluginType;

public enum PluginRealType implements PluginType
{
  FOLDER_ORGANIZER { public String toString() { return "Folder Organizer"; } },
  ROMSET_CLEANUP { public String toString() { return "Romset Cleanup"; } }
}
