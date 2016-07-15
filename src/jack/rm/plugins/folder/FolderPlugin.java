package jack.rm.plugins.folder;

import java.nio.file.Path;
import java.util.function.Function;

import com.pixbits.plugin.PluginType;

import jack.rm.data.rom.Rom;
import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class FolderPlugin extends OrganizerPlugin implements Function<Rom, Path>
{
  public abstract Path getFolderForRom(Rom rom);
  public final Path apply(Rom rom) { return getFolderForRom(rom); }
  
  @Override public String getTitle() { return "Organizing by folder"; }
  @Override public String getProgressText() { return "Moving"; }
    
  @Override public PluginType<?> getPluginType() { return PluginRealType.FOLDER_ORGANIZER; }
}
