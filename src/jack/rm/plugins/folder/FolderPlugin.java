package jack.rm.plugins.folder;

import jack.rm.data.Rom;
import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

import java.nio.file.Path;

import com.pixbits.plugin.PluginType;

public abstract class FolderPlugin extends OrganizerPlugin
{
  public abstract Path getFolderForRom(Rom rom);
    
  @Override public PluginType getPluginType() { return PluginRealType.FOLDER_ORGANIZER; }
}
