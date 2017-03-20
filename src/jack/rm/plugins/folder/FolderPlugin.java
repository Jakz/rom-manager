package jack.rm.plugins.folder;

import java.nio.file.Path;
import java.util.function.Function;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.plugins.OrganizerPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class FolderPlugin extends OrganizerPlugin implements Function<Game, Path>
{
  public abstract Path getFolderForRom(Game rom);
  public final Path apply(Game rom) { return getFolderForRom(rom); }
  
  @Override public String getTitle() { return "Organizing by folder"; }
  @Override public String getProgressText() { return "Moving"; }
    
  @Override public PluginType<?> getPluginType() { return PluginRealType.FOLDER_ORGANIZER; }
}
