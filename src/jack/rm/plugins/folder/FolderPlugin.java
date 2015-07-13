package jack.rm.plugins.folder;

import jack.rm.data.Rom;
import jack.rm.plugin.*;

import java.nio.file.Path;

public abstract class FolderPlugin extends Plugin
{
  public abstract Path getFolderForRom(Rom rom);
  
  @Override public PluginType getType() { return PluginRealType.FOLDER_ORGANIZER; }
}
