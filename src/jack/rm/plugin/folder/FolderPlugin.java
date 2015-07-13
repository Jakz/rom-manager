package jack.rm.plugin.folder;

import jack.rm.data.Rom;
import jack.rm.plugin.Plugin;

import java.nio.file.Path;

public abstract class FolderPlugin extends Plugin
{
  public abstract Path getFolderForRom(Rom rom);
}
